package com.github.codehorde.common.bean.translate;

/**
 * Created by baomingfeng at 2018-04-28 12:57:39
 */
public class StringTranslator implements PropertyTranslator<String> {

    @Override
    public String convert(Object propValue, Class targetType) {
        if (String.class != propValue.getClass()) {
            if (propValue instanceof Enum) {
                return ((Enum) propValue).name();
            } else {
                return String.valueOf(propValue);
            }
        }

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in convert [" + propValue + "] to " + targetType.getName());
    }
}
