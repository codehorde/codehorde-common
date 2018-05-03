package com.github.codehorde.common.bean.translate;

/**
 * Created by baomingfeng at 2018-04-28 12:57:39
 */
public class EnumTranslator implements PropertyTranslator<Enum<?>> {

    public Enum<?> convert(Object sourcePropValue, Class targetPropClass,
                           Object context, Object sourceObject, Object targetObject) {
        if (sourcePropValue instanceof String) {
            return Enum.valueOf(targetPropClass, (String) sourcePropValue);
        }

        //noinspection unchecked
        if (targetPropClass.isAssignableFrom(sourcePropValue.getClass())) {
            return (Enum<?>) sourcePropValue;
        }

        //if (sourcePropValue instanceof Number) -- 数字转Enum?

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in convert [" + sourcePropValue + "] to " + targetPropClass.getName());
    }
}
