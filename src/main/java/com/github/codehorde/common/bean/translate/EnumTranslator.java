package com.github.codehorde.common.bean.translate;

/**
 * Created by baomingfeng at 2018-04-28 12:57:39
 */
public class EnumTranslator implements PropertyTranslator<Enum<?>> {

    public Enum<?> convert(Object propValue, Class targetType) {
        if (propValue instanceof String) {
            return Enum.valueOf(targetType, (String) propValue);
        }

        //noinspection unchecked
        if (targetType.isAssignableFrom(propValue.getClass())) {
            return (Enum<?>) propValue;
        }

        //if (propValue instanceof Number) -- 数字转Enum?

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in convert [" + propValue + "] to " + targetType.getName());
    }
}
