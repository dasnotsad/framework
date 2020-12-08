package net.dasnotsad.framework.tac.data.conditions;

/**
 * @Description: 条件构造抽象类
 * @Author Created by HOT SUN on 2020/7/22 .
 **/
public abstract class AbstractCondition<T, R, Children extends AbstractCondition<T, R, Children>> implements ICondition<T> {
}
