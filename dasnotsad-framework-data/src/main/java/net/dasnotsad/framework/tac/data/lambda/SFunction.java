package net.dasnotsad.framework.tac.data.lambda;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 继承Serializable的Function定义
 *
 * @author liuliwei
 * @create 2020-08-28
 */
@FunctionalInterface
public interface SFunction<T, R> extends Function<T, R>, Serializable {
}
