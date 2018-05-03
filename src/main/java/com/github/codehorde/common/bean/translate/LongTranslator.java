package com.github.codehorde.common.bean.translate;

/**
 * Created by baomingfeng at 2018-04-28 12:57:39
 */
public class LongTranslator implements PropertyTranslator<Long> {

    public Long convert(Object sourcePropValue, Class targetPropClass,
                        Object context, Object sourceObject, Object targetObject) {
        if (sourcePropValue instanceof String) {
            return Long.parseLong((String) sourcePropValue);
        }

        if (sourcePropValue instanceof Number) {
            return ((Number) sourcePropValue).longValue();
        }

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in convert [" + sourcePropValue + "] to " + targetPropClass.getName());
    }
}
