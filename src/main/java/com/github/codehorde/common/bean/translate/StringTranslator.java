package com.github.codehorde.common.bean.translate;

import com.github.codehorde.common.bean.support.PropertyTranslator;

import java.lang.reflect.Type;

/**
 * Created by baomingfeng at 2018-04-28 12:57:39
 */
public class StringTranslator implements PropertyTranslator<String> {

    @Override
    public String translate(Object sourcePropValue, Type targetPropType, Object context) {
        if (String.class != sourcePropValue.getClass()) {
            if (sourcePropValue instanceof Enum) {
                return ((Enum) sourcePropValue).name();
            } else {
                return String.valueOf(sourcePropValue);
            }
        }

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in translate [" + sourcePropValue + "] to " + targetPropType.toString());
    }
}
