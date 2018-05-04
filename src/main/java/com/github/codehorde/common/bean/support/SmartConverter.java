package com.github.codehorde.common.bean.support;

import net.sf.cglib.core.Converter;

import java.lang.reflect.ParameterizedType;

/**
 * <pre>
 *     补充了一部分自动处理的方式
 *          1：目标对象同名属性类型为String，源对象属性转换
 * </pre>
 */
public class SmartConverter implements Converter {

    private final Class targetClass;

    public SmartConverter(Class targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    public Object convert(Object sourcePropValue, Class targetPropClass, Object context) {
        if (sourcePropValue == null) {
            return null;
        }

        Class<?> sourcePropClass = sourcePropValue.getClass();
        if (!ClassHelper.matchCompatible(sourcePropClass, targetPropClass)) {
            PropertyTranslator propertyTranslator = TranslatorRegistry.findPropertyTranslator(targetPropClass);
            if (propertyTranslator != null) {
                ParameterizedType methodParameterType = ClassHelper
                        .getMethodParameterType(targetClass, (String) context, targetPropClass);
                if (methodParameterType == null) {
                    return propertyTranslator.translate(sourcePropValue, targetPropClass, context);
                } else {
                    return propertyTranslator.translate(sourcePropValue, methodParameterType, context);
                }
            }
        }

        return sourcePropValue;
    }


}