package net.dgg.utils.file;

import net.dgg.utils.exception.DggUtilException;
import net.dgg.utils.string.DggStringUtil;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: dgg-linhongda
 * @Date: 2019/6/24 0024
 * @Description: 文件工具类
 */
public class DggFileUtil {

    private static final String FILE_MESSAGE = "当前文件不存在或者不是常规文件!";
    private static final String DIRECTORY_MESSAGE = "当前目录不存在!";
    private static final String DIRECTORY_ILLEGAL_MESSAGE = "当前目录不存在或者不是目录!";


    /**
     * 将流文件转成base64 字符串
     *
     * @param stream 输出流
     * @return *
     */
    public static String encodeBase64Stream(ByteArrayOutputStream stream) {
        return new BASE64Encoder().encode(stream.toByteArray());
    }

    /**
     * 将文件转成base64 字符串
     *
     * @param path 文件路径
     * @return *
     * @throws Exception
     */
    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return new BASE64Encoder().encode(buffer);
    }

    /**
     * 将base64字符解码保存文件
     *
     * @param base64Code
     * @param targetPath
     * @throws Exception
     */
    public static void decoderBase64File(String base64Code, String targetPath, String catalogue)
            throws Exception {
        File file = new File(catalogue);
        if (file.exists() == false) {
            file.mkdirs();
        }
        byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
        FileOutputStream out = new FileOutputStream(targetPath);
        out.write(buffer);
        out.close();
    }

    /**
     * @param path    路径
     * @param message 异常信息
     * @return
     * @description: 获取文件path
     */
    private static Path getPath(String path, String message) {
        Path paths = Paths.get(path);
        if (!Files.exists(paths) || !Files.isRegularFile(paths)) {
            throw new DggUtilException(message);
        }
        return paths;
    }

    /**
     * @param directoryPath 路径
     * @param message       异常信息
     * @return
     * @description: 获取目录path
     */
    private static Path getDirectoryPath(String directoryPath, String message) {
        Path paths = Paths.get(directoryPath);
        if (!Files.exists(paths) || !Files.isDirectory(paths)) {
            throw new DggUtilException(message);
        }
        return paths;
    }

    /**
     * @param path 路径
     * @return
     * @description: 判断文件是否存在
     */
    public static boolean isFile(String path) {
        getPath(path, FILE_MESSAGE);
        return true;
    }

    /**
     * @param path 路径
     * @return
     * @description: 判断目录是否存在
     */
    public static boolean isDirectory(String path) {
        getDirectoryPath(path, DIRECTORY_MESSAGE);
        return true;
    }

    /**
     * @param path 路径
     * @return
     * @description: 创建目录
     */
    public static boolean createDirectory(String path) {
        Path paths = Paths.get(path);
        if (!Files.exists(paths)) {
            try {
                Files.createDirectories(paths);
            } catch (IOException e) {
                throw new DggUtilException("创建目录出错,请查看路径是否正确!");
            }
        }
        return true;
    }

    /**
     * @param path 路径
     * @return
     * @description: 创建文件
     */
    public static boolean createFile(String path) {
        return createFile(path, true);
    }

    /**
     * @param path 路径
     * @param flag 为true,路径不存在直接创建;为false, 路径不存在不创建
     * @return
     * @description: 创建文件
     */
    public static boolean createFile(String path, boolean flag) {
        Path paths = Paths.get(path);
        if (!Files.exists(paths)) {
            String directoryPath = path.substring(0, path.lastIndexOf(File.separator));
            if (!Files.exists(Paths.get(directoryPath))) {
                if (!flag) {
                    throw new DggUtilException("当前目录不存在!");
                }
                createDirectory(directoryPath);
            }
            try {
                Files.createFile(paths);
            } catch (IOException e) {
                throw new DggUtilException("创建文件出错,稍后重试!");
            }
        }
        return true;
    }

    /**
     * @param path 路径
     * @return
     * @description: 删除文件
     */
    public static boolean deleteFile(String path) {
        Path paths = Paths.get(path);
        try {
            Files.deleteIfExists(paths);
        } catch (IOException e) {
            e.printStackTrace();
            throw new DggUtilException("文件删除失败,请稍候重试!");
        }
        return true;
    }


    /**
     * @param sourceFilePath 源文件路径
     * @param distPath       复制路径
     * @return
     * @description: 文件复制(同名文件覆盖)
     */
    public static boolean copyFile(String sourceFilePath, String distPath) {
        return copyFile(sourceFilePath, distPath, null, true);
    }


    /**
     * @param sourceFilePath 源文件路径
     * @param distPath       目标路径
     * @param distFileName   目标文件名称(为空时使用源文件名称)
     * @return
     * @description: 文件复制(同名文件覆盖)
     */
    public static boolean copyFile(String sourceFilePath, String distPath, String distFileName) {
        return copyFile(sourceFilePath, distPath, distFileName, true);
    }

    /**
     * @param sourceFilePath 源文件路径
     * @param targetPath     目标路径
     * @param coverFlag      是否覆盖(true: 覆盖, false: 不覆盖)
     * @return
     * @description: 文件复制
     */
    public static boolean copyFile(String sourceFilePath, String targetPath, String distFileName, boolean coverFlag) {
        if (isFile(sourceFilePath)) {
            Path target = Paths.get(targetPath);
            // 指定目标路径不存在 创建此路径
            if (!Files.exists(target)) {
                createDirectory(targetPath);
            }
            Path sourcePath = Paths.get(sourceFilePath);
            Path distFilePath = DggStringUtil.isEmpty(distFileName) ? target.resolve(sourcePath.getFileName()) : target.resolve(distFileName);
            try {
                if (coverFlag) {
                    Files.copy(sourcePath, distFilePath, StandardCopyOption.REPLACE_EXISTING);
                } else {
                    Files.copy(sourcePath, distFilePath);
                }
            } catch (IOException e) {
                throw new DggUtilException("请确认路径是否正确或者目标路径下已经包含同名文件!");
            }
            return true;
        } else {
            throw new DggUtilException("源文件不存在或者路径不正确!");
        }

    }

    /**
     * @param sourceFilePath 源文件路径
     * @param targetPath     目标路径
     * @return
     * @description: 文件移动(若目标路径存在此源文件同名文件, 默认执行文件覆盖)
     */
    public static boolean moveFile(String sourceFilePath, String targetPath) {
        return moveFile(sourceFilePath, targetPath, true);
    }

    /**
     * @param sourceFilePath 源文件路径
     * @param targetPath     目标路径
     * @param coverFlag      是否覆盖同名文件
     * @return
     * @description: 文件移动
     */
    public static boolean moveFile(String sourceFilePath, String targetPath, boolean coverFlag) {
        Path sourcePath = getPath(sourceFilePath, FILE_MESSAGE);
        Path target = Paths.get(targetPath);
        // 指定目标路径不存在 创建此路径
        if (!Files.exists(target)) {
            createDirectory(targetPath);
        }
        try {
            if (coverFlag) {
                Files.move(sourcePath, target.resolve(sourcePath.getFileName()), StandardCopyOption.ATOMIC_MOVE);
            } else {
                Files.move(sourcePath, target.resolve(sourcePath.getFileName()));
            }

        } catch (IOException e) {
            throw new DggUtilException("文件已存在!");
        }
        return true;
    }

    /**
     * @param directoryPath 目录路径
     * @return
     * @description: 获取目录下的文件总数
     */
    public static long fileCount(String directoryPath) {
        Path path = getDirectoryPath(directoryPath, DIRECTORY_ILLEGAL_MESSAGE);
        try {
            return Files.list(path).count();
        } catch (IOException e) {
            throw new DggUtilException("获取文件总数错误, 请稍候重试!");
        }
    }

    /**
     * @param directoryPath 目录路径
     * @return
     * @description: 获取目录下的所有文件名称(不包含文件夹)
     */
    public static List<String> fileList(String directoryPath) {
        Path path = getDirectoryPath(directoryPath, DIRECTORY_ILLEGAL_MESSAGE);
        List<String> stringList = new ArrayList<>();
        try {
            Files.list(path).forEach(p -> {
                if (Files.isRegularFile(p)) {
                    stringList.add(p.getFileName().toString());
                }
            });
        } catch (IOException e) {
            throw new DggUtilException("获取文件列表错误, 请稍候重试!");
        }
        return stringList;
    }

    /**
     * @param filePath 文件路径
     * @return
     * @description: 读取文件内容(默认为UTF - 8格式编码)
     */
    public static List<String> readFileContent(String filePath) {
        return readFileContent(filePath, Charset.defaultCharset());
    }

    /**
     * @param filePath 文件路径
     * @param charset  文件编码格式
     * @return
     * @description: 读取文件内容
     */
    public static List<String> readFileContent(String filePath, Charset charset) {
        Path path = getPath(filePath, FILE_MESSAGE);
        try {
            return Files.readAllLines(path, DggStringUtil.isEmpty(charset) ? Charset.defaultCharset() : charset);
        } catch (IOException e) {
            if (e instanceof MalformedInputException) {
                throw new DggUtilException("编码格式不正确!");
            }
            throw new DggUtilException("读取文件出错, 请稍候重试!");
        }
    }

    /**
     * @param filePath 文件路径
     * @param content  内容
     * @return
     * @description: 追加内容到文件中(文件不存在自动进行创建)
     */
    public static boolean writeContent(String filePath, List<String> content) {
        if (content == null || content.size() == 0) {
            throw new DggUtilException("内容不能为空!");
        }
        Path path = Paths.get(filePath);
        // 文件存在在末尾追加空行 填充数据
        if (Files.exists(Paths.get(filePath))) {
            try (BufferedWriter bfw = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
                for (int i = 0; i < content.size(); i++) {
                    bfw.newLine();
                    bfw.write(content.get(i));
                    bfw.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // 文件不存在先创建此文件, 并去除最后空格行
            if (createFile(filePath)) {
                try (BufferedWriter bfw = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
                    for (int i = 0; i < content.size(); i++) {
                        if (i == (content.size() - 1)) {
                            bfw.write(content.get(i));
                        } else {
                            bfw.write(content.get(i));
                            bfw.newLine();
                        }
                        bfw.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}
