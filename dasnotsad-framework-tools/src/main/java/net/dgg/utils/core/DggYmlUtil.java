package net.dgg.utils.core;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * @Description: 读取yml文件
 * @Author Created by yan.x on 2019-05-05 .
 **/
public final class DggYmlUtil {

    /**
     * 读取配置文件绑定到对象
     *
     * @param fileName
     * @param cls
     * @param
     * @return
     */
    public static <T> T load(String fileName, Class<T> cls) {
        Yaml yaml = new Yaml(new Constructor(cls));
        try {
            InputStream inStream = new FileInputStream(new File(fileName));
            return yaml.loadAs(inStream, cls);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 读取配置文件绑定到对象
     *
     * @param inStream
     * @param cls
     * @param
     * @return
     */
    public static <T> T load(InputStream inStream, Class<T> cls) {
        Yaml yaml = new Yaml(new Constructor(cls));
        try {
            return yaml.loadAs(inStream, cls);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 读取配置文件转换成map
     *
     * @param fileName
     * @return
     */
    public static Map load(String fileName) {
        Yaml yaml = new Yaml();
        try {
            InputStream inStream = new FileInputStream(new File(fileName));
            Iterable iterable = yaml.loadAll(inStream);
            return (Map) iterable.iterator().next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取配置文件转换成map
     *
     * @param inStream
     * @return
     */
    public static Map load(InputStream inStream) {
        Yaml yaml = new Yaml();
        try {
            Iterable iterable = yaml.loadAll(inStream);
            return (Map) iterable.iterator().next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
