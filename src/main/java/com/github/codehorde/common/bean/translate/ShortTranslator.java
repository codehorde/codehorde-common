package com.github.codehorde.common.bean.translate;

/**
 * Created by baomingfeng at 2018-04-28 12:57:39
 */
public class ShortTranslator implements PropertyTranslator<Short> {

    public Short convert(Object sourcePropValue, Class targetPropClass,
                         Object context, Object sourceObject, Object targetObject) {
        if (sourcePropValue instanceof String) {
            return Short.parseShort((String) sourcePropValue);
        }

        if (sourcePropValue instanceof Number) {
            return ((Number) sourcePropValue).shortValue();
        }

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in convert [" + sourcePropValue + "] to " + targetPropClass.getName());
    }
}
