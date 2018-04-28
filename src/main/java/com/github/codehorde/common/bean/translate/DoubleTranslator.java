package com.github.codehorde.common.bean.translate;

/**
 * Created by baomingfeng at 2018-04-28 12:57:39
 */
public class DoubleTranslator implements PropertyTranslator<Double> {

    public Double convert(Object propValue, Class targetType) {
        if (propValue instanceof String) {
            return Double.parseDouble((String) propValue);
        }

        if (propValue instanceof Number) {
            return ((Number) propValue).doubleValue();
        }

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in convert [" + propValue + "] to " + targetType.getName());
    }
}
