package com.github.codehorde.common.bean.translate;

import com.github.codehorde.common.bean.support.PropertyTranslator;

import java.lang.reflect.Type;

/**
 * Created by baomingfeng at 2018-04-28 12:57:39
 */
public class FloatTranslator implements PropertyTranslator<Float> {

    @Override
    public Float translate(Object sourcePropValue, Type targetPropType, Object context) {
        if (sourcePropValue instanceof String) {
            return Float.parseFloat((String) sourcePropValue);
        }

        if (sourcePropValue instanceof Number) {
            return ((Number) sourcePropValue).floatValue();
        }

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in translate [" + sourcePropValue + "] to " + targetPropType.toString());
    }
}
