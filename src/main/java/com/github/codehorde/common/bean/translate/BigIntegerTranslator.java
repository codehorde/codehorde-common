package com.github.codehorde.common.bean.translate;

import java.math.BigInteger;

/**
 * Created by baomingfeng at 2018-04-28 12:57:39
 */
public class BigIntegerTranslator implements PropertyTranslator<BigInteger> {

    public BigInteger convert(Object propValue, Class targetType) {
        if (propValue instanceof String) {
            return new BigInteger((String) propValue);
        }

        if (propValue instanceof Number) {
            return BigInteger.valueOf(((Number) propValue).longValue());
        }

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in convert [" + propValue + "] to " + targetType.getName());
    }
}
