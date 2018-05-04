package com.github.codehorde.common.bean.translate;

import com.github.codehorde.common.bean.BeanCopierHelper;
import com.github.codehorde.common.bean.support.ClassHelper;
import com.github.codehorde.common.bean.support.PropertyTranslator;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by baomingfeng at 2018-05-02 13:54:09
 */
public class MapTranslator implements PropertyTranslator<Map<?, ?>> {

    @Override
    public Map<?, ?> translate(Object sourcePropValue, Type targetPropType, Object context) {
        if (sourcePropValue instanceof Map) {
            Map<?, ?> sourceMap = (Map<?, ?>) sourcePropValue;
            Class<?> keyClass = null;
            Class<?> valueClass = null;


            if (targetPropType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) targetPropType;
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                if (typeArguments.length > 0) {
                    Type keyType = typeArguments[0];
                    keyClass = ClassHelper.getWrapClass(keyType);
                }
                if (typeArguments.length > 1) {
                    Type valueType = typeArguments[1];
                    valueClass = ClassHelper.getWrapClass(valueType);
                }
            }

            HashMap retMap = new HashMap();
            for (Map.Entry<?, ?> entry : sourceMap.entrySet()) {
                Object sourceKey = entry.getKey();
                Object sourceValue = entry.getValue();
                if (keyClass == null) {
                    keyClass = sourceKey.getClass();
                }
                if (valueClass == null) {
                    valueClass = sourceValue.getClass();
                }
                Object targetKey;
                if (ClassHelper.isBasicClass(keyClass)) {
                    targetKey = sourceKey;
                } else {
                    targetKey = BeanCopierHelper.createBean(sourceKey, keyClass);
                }
                Object targetValue;
                if (ClassHelper.isBasicClass(valueClass)) {
                    targetValue = sourceValue;
                } else {
                    targetValue = BeanCopierHelper.createBean(sourceValue, valueClass);
                }
                //noinspection unchecked
                retMap.put(targetKey, targetValue);
            }
            return retMap;
        }

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in translate [" + sourcePropValue + "] to " + targetPropType.toString());
    }
}
