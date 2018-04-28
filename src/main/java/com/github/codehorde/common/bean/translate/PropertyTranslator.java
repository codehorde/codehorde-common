package com.github.codehorde.common.bean.translate;

/**
 * Created by baomingfeng at 2018-04-28 12:51:16
 */
public interface PropertyTranslator<T> {

    /**
     * value != null
     *
     * @param propValue  复制的源对象属性值
     * @param targetType 复制的目标对象属性类型
     * @return 属性转换的结果
     */
    T convert(Object propValue, Class targetType);
}