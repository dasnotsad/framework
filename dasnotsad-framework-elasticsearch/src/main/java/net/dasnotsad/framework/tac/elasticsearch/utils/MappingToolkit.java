package net.dasnotsad.framework.tac.elasticsearch.utils;

import lombok.SneakyThrows;
import net.dasnotsad.framework.tac.elasticsearch.annotation.EsField;
import net.dasnotsad.framework.tac.elasticsearch.core.enums.FieldType;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Mapping映射对象工具类
 * @author liuliwei
 * @since 2020-12-2
 */
public class MappingToolkit {

    /**
     * 解析输入的类EsField注解，生成Mapping映射对象
     *
     * @param clazz 输入类
     * @return XContentBuilder 生成Mapping映射对象
     */
    @SneakyThrows
    public static XContentBuilder createMapping(Class clazz) {
        Objects.requireNonNull(clazz, "clazz must not be null");
        BeanUtil.Node node = BeanUtil.treeNode(clazz);
        XContentBuilder mapping = null;
        if(node.getNodes().size() > 0){
            mapping = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("properties");
            for(BeanUtil.Node subNode : node.getNodes()) {
                addMappingNode(subNode, mapping);
            }
            mapping.endObject()
                    .endObject();
        }
        return mapping;
    }

    @SneakyThrows
    private static void addMappingNode(BeanUtil.Node node, XContentBuilder mapping) {
        //当前节点存在属性
        if(node != null && node.getField() != null) {
            Field field = node.getField();
            field.setAccessible(true);
            EsField esField = field.getAnnotation(EsField.class);
            //没标注ES注解则不予创建mapping
            if (esField != null) {
                mapping.startObject(field.getName());
                if(FieldType.text_nokeyword_type.getCode().equals(esField.fieldType().getCode())) {
                    mapping.field("type", "text");
                }else {
                    mapping.field("type", esField.fieldType().getCode());
                }
                //ES默认text类型设置
                if(FieldType.text_type.getCode().equals(esField.fieldType().getCode())) {
                    Map<String, Map<String, Object>> values = new HashMap<>();
                    Map<String, Object> keyword = new HashMap<>();
                    keyword.put("ignore_above", 256);
                    keyword.put("type", "keyword");
                    values.put("keyword", keyword);
                    mapping.field("fields", values);
                }
                if(!StringUtils.isEmpty(esField.analyzer().getCode()))
                    mapping.field("analyzer", esField.analyzer().getCode());
                if(node.getNodes() != null && node.getNodes().size() > 0) {
                    mapping.startObject("properties");
                    for(BeanUtil.Node subNode : node.getNodes()) {
                        addMappingNode(subNode, mapping);
                    }
                    mapping.endObject();
                }
                mapping.endObject();
            }
        }
    }
}
