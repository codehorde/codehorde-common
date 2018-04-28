package com.github.codehorde.common.bean.translate;

/**
 * Created by baomingfeng at 2018-04-28 12:57:39
 */
public class FloatTranslator implements PropertyTranslator<Float> {

    public Float convert(Object propValue, Class targetType) {
        if (propValue instanceof String) {
            return Float.parseFloat((String) propValue);
        }

        if (propValue instanceof Number) {
            return ((Number) propValue).floatValue();
        }

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in convert [" + propValue + "] to " + targetType.getName());
    }
}
