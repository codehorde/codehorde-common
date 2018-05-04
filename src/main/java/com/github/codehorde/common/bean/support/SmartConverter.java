package com.github.codehorde.common.bean.support;

import net.sf.cglib.core.Converter;

/**
 * <pre>
 *     补充了一部分自动处理的方式
 *          1：目标对象同名属性类型为String，源对象属性转换
 * </pre>
 */
public class SmartConverter implements Converter {

    @Override
    public Object convert(Object sourcePropValue, Class targetPropClass, Object context) {
        if (sourcePropValue == null) {
            return null;
        }

        Class<?> sourcePropClass = sourcePropValue.getClass();
        if (!ClassHelper.matchCompatible(sourcePropClass, targetPropClass)) {
            PropertyTranslator propertyTranslator = TranslatorRegistry.findPropertyTranslator(targetPropClass);
            if (propertyTranslator != null) {
                return propertyTranslator.translate(sourcePropValue, targetPropClass, context);
            }
        }

        return sourcePropValue;
    }


}