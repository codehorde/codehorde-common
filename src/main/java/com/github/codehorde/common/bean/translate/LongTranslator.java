package com.github.codehorde.common.bean.translate;

/**
 * Created by baomingfeng at 2018-04-28 12:57:39
 */
public class LongTranslator implements PropertyTranslator<Long> {

    public Long convert(Object propValue, Class targetType) {
        if (propValue instanceof String) {
            return Long.parseLong((String) propValue);
        }

        if (propValue instanceof Number) {
            return ((Number) propValue).longValue();
        }

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in convert [" + propValue + "] to " + targetType.getName());
    }
}
