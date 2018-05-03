package com.github.codehorde.common.bean.translate;

/**
 * Created by baomingfeng at 2018-04-28 12:57:39
 */
public class FloatTranslator implements PropertyTranslator<Float> {

    public Float convert(Object sourcePropValue, Class targetPropClass,
                         Object context, Object sourceObject, Object targetObject) {

        if (sourcePropValue instanceof String) {
            return Float.parseFloat((String) sourcePropValue);
        }

        if (sourcePropValue instanceof Number) {
            return ((Number) sourcePropValue).floatValue();
        }

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in convert [" + sourcePropValue + "] to " + targetPropClass.getName());
    }
}
