package com.github.codehorde.common.bean.translate;

/**
 * Created by baomingfeng at 2018-04-28 12:57:39
 */
public class IntegerTranslator implements PropertyTranslator<Integer> {

    public Integer convert(Object propValue, Class targetType) {
        if (propValue instanceof String) {
            return Integer.parseInt((String) propValue);
        }

        if (propValue instanceof Number) {
            return ((Number) propValue).intValue();
        }

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in convert [" + propValue + "] to " + targetType.getName());
    }
}
