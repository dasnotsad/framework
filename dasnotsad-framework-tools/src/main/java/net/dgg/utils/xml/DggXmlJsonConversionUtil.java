package net.dgg.utils.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import net.sf.json.JSON;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @ClassName: DggXmlJsonConversionUtil
 * @Description: XML、JSON 转換
 * @Author: jiangsh
 * @Date: 2019/10/29 16:56
 */
public class DggXmlJsonConversionUtil {

    private static final int xmlLength = 38;
    private static final String xmlHead = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    /**
     * JSON(数组)字符串转换成XML字符串（带xml头标签的）
     * 注：xml结果中，自动会添加“<?xml version="1.0" encoding="UTF-8"?>”头
     *                且 json最外层大括号会默认转换成“<o></o>”标签；
     * @param jsonString json字符串
     * @return xml字符串
     */
    public static String jsonToXmlAndHead(String jsonString) {
        XMLSerializer xmlSerializer = new XMLSerializer();
        return xmlSerializer.write(JSONSerializer.toJSON(jsonString));
    }

    /**
     * JSON(数组)字符串转换成XML字符串
     * 注意：json最外层大括号会默认转换成“<o></o>”标签；
     * @param jsonString json字符串
     * @return xml字符串
     */
    public static String jsonToXml(String jsonString) {
        XMLSerializer xmlSerializer = new XMLSerializer();
        return xmlSerializer.write(JSONSerializer.toJSON(jsonString)).substring(xmlLength);
    }

    /**
     * JSON(数组)字符串转换成XML字符串
     * 注：xml结果中，自动会添加“<?xml version="1.0" encoding="UTF-8"?>”头
     * @param jsonString json字符串
     * @return xml字符串
     */
    public static String jsonToXmlHeadAndWithOut(String jsonString) {
        return xmlHead.concat(jsonToXmlWithOut(jsonString));
    }

    /**
     * JSON(数组)字符串转换成XML字符串
     * @param jsonString json字符串
     * @return xml字符串
     */
    public static String jsonToXmlWithOut(String jsonString) {
        XMLSerializer xmlSerializer = new XMLSerializer();
        String str =  xmlSerializer.write(JSONSerializer.toJSON(jsonString));
        int len = str.length();
        return str.substring(xmlLength + 5, len - 6);
    }

    /**
     * XML 转 JSON (未添加外部标签的)
     * 注：json结果中，如果是元素的属性，会在json里的key前加一个“@”标识
     * @param xmlString xml字符串
     * @return json字符串
     */
    public static String xmlToJson(String xmlString) {
        XMLSerializer xmlSerializer = new XMLSerializer();
        JSON json = xmlSerializer.read(xmlString);
        return json.toString(1);
    }


    /**
     * XML 转 JSON (含外部标签的)
     * 注：json结果中，如果是元素的属性，会在json里的key前加一个“@”标识
     * @param xmlString xml字符串
     * @return json字符串
     */
    public static String xmlToJsonWithOut(String xmlString) {
        XMLSerializer xmlSerializer = new XMLSerializer();
        int length = xmlString.length();
        final String head = xmlString.substring(0, xmlLength);
        final String tail = xmlString.substring(xmlLength, length);
        String result = head.concat("<aaa id=\"aid\">").concat(tail).concat("</aaa>");
        JSON json = xmlSerializer.read(result);
        return json.toString(1).replace("\"@id\": \"aid\",", "");
    }


    /**
     * XML转Bean对象
     * @param clazz 对象类
     * @param xml xml字符串
     * @param <T> T
     * @return
     */
    public static <T> T xmlToBean(Class<T> clazz, String xml) {
        XStream xStream = new XStream();
        xStream.alias(clazz.getSimpleName(), clazz);
        //转对象
        @SuppressWarnings("unchecked")
        T t = (T) xStream.fromXML(xml);
        return t;
    }

    private static Map<Class, Class[]> cacheMap = new HashMap<>();

    /**
     * bean对象转XML
     * @param obj 对象
     * @return
     */
    public static String beanToXml(Object obj) {
        Class[] classes;
        XStream xStream = new XStream(new DomDriver());
        if(cacheMap.containsKey(obj.getClass()))
            classes = cacheMap.get(obj.getClass());
        else{
            List<Class> classList = new ArrayList<>();
            Class tempClass = obj.getClass();
            while (tempClass != null) {
                classList.add(tempClass);
                tempClass = tempClass.getSuperclass();
            }
            classes = new Class[classList.size()];
            classList.toArray(classes);
            cacheMap.put(obj.getClass(), classes);
        }
        xStream.processAnnotations(classes);
        return xStream.toXML(obj);
    }

}
