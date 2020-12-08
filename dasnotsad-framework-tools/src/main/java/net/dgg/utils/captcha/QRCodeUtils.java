package net.dgg.utils.captcha;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Random;

/**
 * @Description: 二维码工具类
 * @Author Created by yan.x on 2019-06-02 .
 **/
public class QRCodeUtils {
    private static final Logger logger = LoggerFactory.getLogger(QRCodeUtils.class);

    private static final String CHARSET = "utf-8";
    private static final String FORMAT_NAME = "JPG";
    // 二维码尺寸
    private static final int QRCODE_SIZE = 1500;
    // LOGO宽度
    private static final int WIDTH = 60;
    // LOGO高度
    private static final int HEIGHT = 60;

    /**
     * 创建图片
     *
     * @param content      内容
     * @param imgPath      图片路径
     * @param needCompress 是否需要压缩
     * @return
     */
    private static BufferedImage createImage(String content, String imgPath, boolean needCompress) {
        try {
            Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            if (imgPath == null || "".equals(imgPath)) {
                return image;
            }
            // 插入图片
            QRCodeUtils.insertImage(image, imgPath, needCompress);
            return image;
        } catch (Exception e) {
            throw new RuntimeException("init.. get QRCodeUtil_createImage is error !", e);
        }
    }

    /**
     * 插入LOGO
     *
     * @param source       二维码图片
     * @param imgPath      LOGO图片地址
     * @param needCompress 是否压缩
     * @throws Exception
     */
    private static void insertImage(BufferedImage source, String imgPath, boolean needCompress) throws Exception {
        File file = new File(imgPath);
        if (!file.exists()) {
            logger.error("QRCodeUtil_insertImage:" + imgPath);
            return;
        }
        Image src = ImageIO.read(new File(imgPath));
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        if (needCompress) {
            if (width > WIDTH) {
                width = WIDTH;
            }
            if (height > HEIGHT) {
                height = HEIGHT;
            }
            Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
            src = image;
        }
        // 插入LOGO
        Graphics2D graph = source.createGraphics();
        int x = (QRCODE_SIZE - width) / 2;
        int y = (QRCODE_SIZE - height) / 2;
        graph.drawImage(src, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }

    /**
     * 生成二维码(内嵌LOGO)
     *
     * @param content      内容
     * @param imgPath      LOGO地址
     * @param destPath     存放目录
     * @param needCompress 是否压缩LOGO
     * @throws Exception
     */
    public static void encode(String content, String imgPath, String destPath, boolean needCompress) throws Exception {
        BufferedImage image = QRCodeUtils.createImage(content, imgPath, needCompress);
        mkdirs(destPath);
        String file = new Random().nextInt(99999999) + ".jpg";
        ImageIO.write(image, FORMAT_NAME, new File(destPath + "/" + file));
    }

    /**
     * 生成二维码(内嵌LOGO)
     *
     * @param content      内容
     * @param logoImgPath  LOGO地址
     * @param destPath     存放目录
     * @param needCompress 是否压缩LOGO
     * @Version
     */
    public static File encodeAndGetFile(String content, String logoImgPath, String destPath, String fileName, boolean needCompress) throws Exception {
        BufferedImage image = QRCodeUtils.createImage(content, logoImgPath, needCompress);
        mkdirs(destPath);
        if (null == fileName || fileName.length() == 0) {
            fileName = new Random().nextInt(99999999) + ".jpg";
        }
        File file = new File(destPath + "/" + fileName);
        ImageIO.write(image, FORMAT_NAME, file);
        return file;
    }

    /**
     * 当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir．(mkdir如果父目录不存在则会抛出异常)
     *
     * @param destPath 存放目录
     */
    public static void mkdirs(String destPath) {
        File file = new File(destPath);
        //当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir．(mkdir如果父目录不存在则会抛出异常)
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
    }

    /**
     * 生成二维码(内嵌LOGO)
     *
     * @param content  内容
     * @param imgPath  LOGO地址
     * @param destPath 存储地址
     * @throws Exception
     */
    public static void encode(String content, String imgPath, String destPath) throws Exception {
        QRCodeUtils.encode(content, imgPath, destPath, false);
    }

    /**
     * 生成二维码
     *
     * @param content      内容
     * @param destPath     存储地址
     * @param needCompress 是否压缩LOGO
     * @throws Exception
     */
    public static void encode(String content, String destPath, boolean needCompress) throws Exception {
        QRCodeUtils.encode(content, null, destPath, needCompress);
    }

    /**
     * 生成二维码
     *
     * @param content  内容
     * @param destPath 存储地址
     * @throws Exception
     */
    public static void encode(String content, String destPath) throws Exception {
        QRCodeUtils.encode(content, null, destPath, false);
    }

    /**
     * 生成二维码(内嵌LOGO)
     *
     * @param content      内容
     * @param imgPath      LOGO地址
     * @param output       输出流
     * @param needCompress 是否压缩LOGO
     * @throws Exception
     */
    public static void encode(String content, String imgPath, OutputStream output, boolean needCompress) throws Exception {
        BufferedImage image = QRCodeUtils.createImage(content, imgPath, needCompress);
        ImageIO.write(image, FORMAT_NAME, output);
    }

    /**
     * 生成二维码
     *
     * @param content 内容
     * @param output  输出流
     * @throws Exception
     */
    public static void encode(String content, OutputStream output) throws Exception {
        QRCodeUtils.encode(content, null, output, false);
    }

    /**
     * 解析二维码
     *
     * @param file 二维码图片
     * @return
     * @throws Exception
     */
    public static String decode(File file) throws Exception {
        BufferedImage image = ImageIO.read(file);
        if (image == null) {
            return null;
        }
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result;
        Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
        hints.put(DecodeHintType.CHARACTER_SET, CHARSET);
        result = new MultiFormatReader().decode(bitmap, hints);
        String resultStr = result.getText();
        return resultStr;
    }

    /**
     * 解析二维码
     *
     * @param path 二维码图片地址
     * @return
     * @throws Exception
     */
    public static String decode(String path) throws Exception {
        return QRCodeUtils.decode(new File(path));
    }

    public static void main(String[] args) {
//        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String destPath = "C:\\Users\\Administrator\\Pictures";
        try {
            QRCodeUtils.encode("135689710128", destPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        byte[] bytes = output.toByteArray();
//        FileImageOutputStream imageOutput = new FileImageOutputStream(new File(path));
//        String encode = QRCodeUtils.encode(bytes);
//        System.err.println(encode);
    }
}