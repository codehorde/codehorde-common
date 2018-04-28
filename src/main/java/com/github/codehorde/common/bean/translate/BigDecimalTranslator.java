package com.github.codehorde.common.bean.translate;

import java.math.BigDecimal;

/**
 * Created by baomingfeng at 2018-04-28 12:57:39
 */
public class BigDecimalTranslator implements PropertyTranslator<BigDecimal> {

    public BigDecimal convert(Object propValue, Class targetType) {
        if (propValue instanceof String) {
            return new BigDecimal((String) propValue);
        }

        if (propValue instanceof Number) {
            return BigDecimal.valueOf(((Number) propValue).longValue());
        }

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in convert [" + propValue + "] to " + targetType.getName());
    }
}
