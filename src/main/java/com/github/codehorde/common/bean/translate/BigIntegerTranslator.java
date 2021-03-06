package com.github.codehorde.common.bean.translate;

import com.github.codehorde.common.bean.support.PropertyTranslator;

import java.lang.reflect.Type;
import java.math.BigInteger;

/**
 * Created by baomingfeng at 2018-04-28 12:57:39
 */
public class BigIntegerTranslator implements PropertyTranslator<BigInteger> {

    @Override
    public BigInteger translate(Object sourcePropValue, Type targetPropType) {
        if (sourcePropValue instanceof String) {
            return new BigInteger((String) sourcePropValue);
        }

        if (sourcePropValue instanceof Number) {
            return BigInteger.valueOf(((Number) sourcePropValue).longValue());
        }

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in translate [" + sourcePropValue + "] to " + targetPropType.toString());
    }
}
