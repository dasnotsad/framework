package net.dgg.utils.captcha;


import net.dgg.utils.captcha.gif.AnimatedGifEncoder;
import net.dgg.utils.constant.StringPool;
import net.dgg.utils.core.DggLogUtil;
import net.dgg.utils.exception.DggAsserts;
import net.dgg.utils.exception.DggUtilException;
import net.dgg.utils.file.DggFileUtil;
import net.dgg.utils.file.DggIoUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

/**
 * @Description: 图形验证码工具类
 * @Author Created by yan.x on 2019-06-20 .
 **/
public final class VerifyCodeUtil {

    /**
     * 生成图形码
     *
     * @param text   ; 待生成文本
     * @param width  : 宽
     * @param height : 高
     * @return
     */
    public final static ByteArrayOutputStream generateGraphicCode(final String text, final int width, final int height) {
        DggAsserts.isNotBlank(text, "文本不能为空");
        ByteArrayOutputStream firstStream = new ByteArrayOutputStream();
        try {
            RandomImageGenerator.render(text, firstStream, width, height);
        } catch (Exception e) {
            DggLogUtil.error("图形码生成失败", e);
            throw new DggUtilException("图形码生成失败");
        }
        return firstStream;
    }

    /**
     * 生成图形码
     *
     * @param text
     * @return
     */
    public final static ByteArrayOutputStream generateGraphicCode(final String text) {
        return generateGraphicCode(text, 245, 40);
    }

    /**
     * 生成GIF图形码
     *
     * @param text   : 内容
     * @param width  : 宽
     * @param height : 高
     * @param count  : 循环次数
     * @param delay  : 帧
     * @return
     */
    public final static ByteArrayOutputStream generateGifGraphicCode(final String text, final int width, final int height, final int count, final int delay) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            AnimatedGifEncoder e = new AnimatedGifEncoder();
            e.start(outputStream);
            e.setDelay(delay);
            for (int i = 0, len = count; i < len; i++) {
                BufferedImage src = ImageIO.read(DggIoUtil.parse(generateGraphicCode(text, width, height)));
                e.addFrame(src);
                e.setDelay(delay);
            }
            e.finish();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return outputStream;
    }

    /**
     * 生成手机GIF图形码
     *
     * @param mobile
     * @return
     */
    public final static String generateMobileGifGraphicCode(final String mobile, final int count, final int delay) {
        ByteArrayOutputStream outputStream = generateGifGraphicCode(mobile, 245, 40, count, delay);
        return String.format(StringPool.FILE_BASE64_TYPE, StringPool.IMAGE_GIF, DggFileUtil.encodeBase64Stream(outputStream));
    }

    /**
     * 生成手机GIF图形码
     *
     * @param mobile
     * @return
     */
    public final static String generateMobileGifGraphicCode(final String mobile) {
        return generateMobileGifGraphicCode(mobile, 4, 100);
    }

    public static void main(String[] args) {
        String img = generateMobileGifGraphicCode("17760522925");
        System.err.println(img);
    }
}