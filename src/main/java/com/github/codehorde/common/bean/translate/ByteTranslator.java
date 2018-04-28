package com.github.codehorde.common.bean.translate;

/**
 * Created by baomingfeng at 2018-04-28 12:57:39
 */
public class ByteTranslator implements PropertyTranslator<Byte> {

    public Byte convert(Object propValue, Class targetType) {
        if (propValue instanceof String) {
            return Byte.parseByte((String) propValue);
        }

        if (propValue instanceof Number) {
            return ((Number) propValue).byteValue();
        }

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in convert [" + propValue + "] to " + targetType.getName());
    }
}
