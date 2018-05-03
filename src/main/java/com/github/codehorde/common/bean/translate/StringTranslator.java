package com.github.codehorde.common.bean.translate;

/**
 * Created by baomingfeng at 2018-04-28 12:57:39
 */
public class StringTranslator implements PropertyTranslator<String> {

    @Override
    public String convert(Object sourcePropValue, Class targetPropClass,
                          Object context, Object sourceObject, Object targetObject) {
        if (String.class != sourcePropValue.getClass()) {
            if (sourcePropValue instanceof Enum) {
                return ((Enum) sourcePropValue).name();
            } else {
                return String.valueOf(sourcePropValue);
            }
        }

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in convert [" + sourcePropValue + "] to " + targetPropClass.getName());
    }
}
