package com.github.codehorde.common.bean.translate;

import java.math.BigDecimal;

/**
 * Created by baomingfeng at 2018-04-28 12:57:39
 */
public class BigDecimalTranslator implements PropertyTranslator<BigDecimal> {

    public BigDecimal convert(Object sourcePropValue, Class targetPropClass,
                              Object context, Object sourceObject, Object targetObject) {
        if (sourcePropValue instanceof String) {
            return new BigDecimal((String) sourcePropValue);
        }

        if (sourcePropValue instanceof Number) {// ?
            return new BigDecimal(String.valueOf(sourcePropValue));
        }

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in convert [" + sourcePropValue + "] to " + targetPropClass.getName());
    }
}
