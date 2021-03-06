package com.github.codehorde.common.bean.translate;

import com.github.codehorde.common.bean.support.PropertyTranslator;

import java.lang.reflect.Type;

/**
 * Created by baomingfeng at 2018-04-28 12:57:39
 */
public class IntegerTranslator implements PropertyTranslator<Integer> {

    @Override
    public Integer translate(Object sourcePropValue, Type targetPropType) {
        if (sourcePropValue instanceof String) {
            return Integer.parseInt((String) sourcePropValue);
        }

        if (sourcePropValue instanceof Number) {
            return ((Number) sourcePropValue).intValue();
        }

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in translate [" + sourcePropValue + "] to " + targetPropType.toString());
    }
}
