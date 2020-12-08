package net.dasnotsad.framework.data;

import java.lang.annotation.*;

/**
 * 定义版本号version的字段，用于update一致性验证
 *
 * @author liuliwei
 * @create 2020-08-05
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface VersionField {
}
