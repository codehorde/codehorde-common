package com.github.codehorde.common.bean.translate;

import java.math.BigInteger;

/**
 * Created by baomingfeng at 2018-04-28 12:57:39
 */
public class BigIntegerTranslator implements PropertyTranslator<BigInteger> {

    public BigInteger convert(Object sourcePropValue, Class targetPropClass,
                              Object context, Object sourceObject, Object targetObject) {
        if (sourcePropValue instanceof String) {
            return new BigInteger((String) sourcePropValue);
        }

        if (sourcePropValue instanceof Number) {
            return BigInteger.valueOf(((Number) sourcePropValue).longValue());
        }

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in convert [" + sourcePropValue + "] to " + targetPropClass.getName());
    }
}
