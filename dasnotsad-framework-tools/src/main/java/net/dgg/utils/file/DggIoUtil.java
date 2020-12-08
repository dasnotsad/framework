package net.dgg.utils.file;

import net.dgg.utils.common.StreamProgress;
import net.dgg.utils.exception.DggUtilException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.FastByteArrayOutputStream;

import java.io.*;
import java.nio.charset.Charset;

/**
 * IO工具类<br>
 * IO工具类只是辅助流的读写，并不负责关闭流。原因是流可能被多次读写，读写关闭后容易造成问题。
 * <p>
 * Created by yan.x on 2019/1/24 .
 **/
public final class DggIoUtil {

    private static final Logger logger = LoggerFactory.getLogger(DggIoUtil.class);

    /**
     * 默认缓存大小
     */
    public static final int DEFAULT_BUFFER_SIZE = 2048;
    /**
     * 默认中等缓存大小
     */
    public static final int DEFAULT_MIDDLE_BUFFER_SIZE = 4096;
    /**
     * 默认大缓存大小
     */
    public static final int DEFAULT_LARGE_BUFFER_SIZE = 8192;

    /**
     * 数据流末尾
     */
    public static final int EOF = -1;

    /**
     * 文件转为流
     *
     * @param file 文件
     * @return {@link FileInputStream}
     */
    public static FileInputStream toStream(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new DggUtilException(e);
        }
    }

    /**
     * 获得一个Reader
     *
     * @param in      输入流
     * @param charset 字符集
     * @return BufferedReader对象
     */
    public static BufferedReader getReader(InputStream in, Charset charset) {
        if (null == in) {
            return null;
        }
        InputStreamReader reader = null;
        if (null == charset) {
            reader = new InputStreamReader(in);
        } else {
            reader = new InputStreamReader(in, charset);
        }
        return new BufferedReader(reader);
    }

    /**
     * 拷贝流，使用默认Buffer大小
     *
     * @param in  输入流
     * @param out 输出流
     * @return 传输的byte数
     * @throws DggUtilException IO异常
     */
    public static long copy(InputStream in, OutputStream out) throws DggUtilException {
        return copy(in, out, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 拷贝流
     *
     * @param in         输入流
     * @param out        输出流
     * @param bufferSize 缓存大小
     * @return 传输的byte数
     * @throws DggUtilException IO异常
     */
    public static long copy(InputStream in, OutputStream out, int bufferSize) throws DggUtilException {
        return copy(in, out, bufferSize, null);
    }

    /**
     * 拷贝流
     *
     * @param in             输入流
     * @param out            输出流
     * @param bufferSize     缓存大小
     * @param streamProgress 进度条
     * @return 传输的byte数
     * @throws DggUtilException IO异常
     */
    public static long copy(InputStream in, OutputStream out, int bufferSize, StreamProgress streamProgress) throws DggUtilException {
        Assert.notNull(in, "InputStream is null !");
        Assert.notNull(out, "OutputStream is null !");
        if (bufferSize <= 0) {
            bufferSize = DEFAULT_BUFFER_SIZE;
        }

        byte[] buffer = new byte[bufferSize];
        if (null != streamProgress) {
            streamProgress.start();
        }
        long size = 0;
        try {
            for (int readSize = -1; (readSize = in.read(buffer)) != EOF; ) {
                out.write(buffer, 0, readSize);
                size += readSize;
                out.flush();
                if (null != streamProgress) {
                    streamProgress.progress(size);
                }
            }
        } catch (IOException e) {
            throw new DggUtilException(e);
        }
        if (null != streamProgress) {
            streamProgress.finish();
        }
        return size;
    }

    public static ByteArrayInputStream parse(OutputStream out) {
        ByteArrayInputStream swapStream;
        try {
            ByteArrayOutputStream baos = (ByteArrayOutputStream) out;
            swapStream = new ByteArrayInputStream(baos.toByteArray());
        } finally {
            close(out);
        }
        return swapStream;
    }

    /**
     * 从流中读取bytes
     *
     * @param in {@link InputStream}
     * @return bytes
     * @throws DggUtilException IO异常
     */
    public static byte[] readBytes(InputStream in) throws DggUtilException {
        final FastByteArrayOutputStream out = new FastByteArrayOutputStream();
        copy(in, out);
        return out.toByteArray();
    }

    /**
     * 读取指定长度的byte数组，不关闭流
     *
     * @param in     {@link InputStream}，为null返回null
     * @param length 长度，小于等于0返回空byte数组
     * @return bytes
     * @throws DggUtilException IO异常
     */
    public static byte[] readBytes(InputStream in, int length) throws DggUtilException {
        if (null == in) {
            return null;
        }
        if (length <= 0) {
            return new byte[0];
        }

        byte[] b = new byte[length];
        int readLength;
        try {
            readLength = in.read(b);
        } catch (IOException e) {
            throw new DggUtilException(e);
        }
        if (readLength > 0 && readLength < length) {
            byte[] b2 = new byte[length];
            System.arraycopy(b, 0, b2, 0, readLength);
            return b2;
        } else {
            return b;
        }
    }

    /**
     * 关闭<br>
     * 关闭失败不会抛出异常
     *
     * @param closeables 被关闭的对象
     */
    public static void close(Closeable... closeables) {
        if (null == closeables || closeables.length < 1) {
            return;
        }
        for (Closeable closeable : closeables) {
            if (null != closeable) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    // 静默关闭
                    logger.error("InputStream关闭异常", e);
                }
            }
        }
    }

    /**
     * 关闭<br>
     * 关闭失败不会抛出异常
     *
     * @param closeables 被关闭的对象
     */
    public static void close(AutoCloseable... closeables) {
        if (null == closeables || closeables.length < 1) {
            return;
        }
        for (AutoCloseable closeable : closeables) {
            if (null != closeable) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    // 静默关闭
                    logger.error("InputStream关闭异常", e);
                }
            }
        }
    }
}
