package com.github.codehorde.common.bean.translate;

/**
 * Created by baomingfeng at 2018-04-28 12:57:39
 */
public class ByteTranslator implements PropertyTranslator<Byte> {

    public Byte convert(Object sourcePropValue, Class targetPropClass,
                        Object context, Object sourceObject, Object targetObject) {
        if (sourcePropValue instanceof String) {
            return Byte.parseByte((String) sourcePropValue);
        }

        if (sourcePropValue instanceof Number) {
            return ((Number) sourcePropValue).byteValue();
        }

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in convert [" + sourcePropValue + "] to " + targetPropClass.getName());
    }
}
