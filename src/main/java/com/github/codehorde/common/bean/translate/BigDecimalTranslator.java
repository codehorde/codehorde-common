package com.github.codehorde.common.bean.translate;

import com.github.codehorde.common.bean.support.PropertyTranslator;

import java.lang.reflect.Type;
import java.math.BigDecimal;

/**
 * Created by baomingfeng at 2018-04-28 12:57:39
 */
public class BigDecimalTranslator implements PropertyTranslator<BigDecimal> {

    @Override
    public BigDecimal translate(Object sourcePropValue, Type targetPropType, Object context) {
        if (sourcePropValue instanceof String) {
            return new BigDecimal((String) sourcePropValue);
        }

        if (sourcePropValue instanceof Number) {
            return new BigDecimal(String.valueOf(sourcePropValue));
        }

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in translate [" + sourcePropValue + "] to " + targetPropType.toString());
    }
}
