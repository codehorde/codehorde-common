package com.github.codehorde.common.bean.translate;

/**
 * Created by baomingfeng at 2018-04-28 12:51:16
 */
public interface PropertyTranslator<T> {

    /**
     * 属性转换器
     *
     * @param sourcePropValue 复制的源对象属性值
     * @param targetPropClass 复制的目标对象属性类型
     * @param context         属性方法名称
     * @param sourceObject    复制的源对象
     * @param targetObject    复制的目标对象
     * @return 属性转换的结果
     */
    T convert(Object sourcePropValue, Class targetPropClass, Object context, Object sourceObject, Object targetObject);
}