package com.github.codehorde.common.bean.translate;

/**
 * Created by baomingfeng at 2018-04-28 12:57:39
 */
public class ShortTranslator implements PropertyTranslator<Short> {

    public Short convert(Object propValue, Class targetType) {
        if (propValue instanceof String) {
            return Short.parseShort((String) propValue);
        }

        if (propValue instanceof Number) {
            return ((Number) propValue).shortValue();
        }

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in convert [" + propValue + "] to " + targetType.getName());
    }
}
