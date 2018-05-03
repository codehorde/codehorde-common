package com.github.codehorde.common.bean.translate;

import com.github.codehorde.common.bean.BeanCopierUtils;
import com.github.codehorde.common.bean.support.ClassHelper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by baomingfeng at 2018-05-02 13:54:09
 */
public class MapTranslator implements PropertyTranslator<Map<?, ?>> {

    @Override
    public Map<?, ?> convert(Object sourcePropValue, Class targetPropClass,
                             Object context, Object sourceObject, Object targetObject) {

        if (sourcePropValue instanceof Map) {
            Map<?, ?> sourceMap = (Map<?, ?>) sourcePropValue;
            Class<?> keyType = null;
            Class<?> valueType = null;

            ParameterizedType parameterizedType = ClassHelper.getMethodParameterType(
                    targetObject.getClass(), (String) context, targetPropClass);
            if (parameterizedType != null) {
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                if (typeArguments.length > 0) {
                    Type type = typeArguments[0];
                    //noinspection ConstantConditions
                    keyType = type instanceof Class ? (Class<?>) type : null;
                }
                if (typeArguments.length > 0) {
                    Type type = typeArguments[1];
                    //noinspection ConstantConditions
                    valueType = type instanceof Class ? (Class<?>) type : null;
                }
            }

            HashMap retMap = new HashMap();
            for (Map.Entry<?, ?> entry : sourceMap.entrySet()) {
                Object sourceKey = entry.getKey();
                Object sourceValue = entry.getValue();
                if (keyType == null) {
                    keyType = sourceKey.getClass();
                }
                if (valueType == null) {
                    valueType = sourceValue.getClass();
                }
                Object targetKey;
                if (ClassHelper.isBasicClass(keyType)) {
                    targetKey = sourceKey;
                } else {
                    targetKey = ClassHelper.instantiate(keyType);
                    BeanCopierUtils.adaptMapping(sourceKey, targetKey);
                }
                Object targetValue;
                if (ClassHelper.isBasicClass(valueType)) {
                    targetValue = sourceValue;
                } else {
                    targetValue = ClassHelper.instantiate(valueType);
                    BeanCopierUtils.adaptMapping(sourceValue, sourceValue);
                }
                //noinspection unchecked
                retMap.put(targetKey, targetValue);
            }
            return retMap;
        }

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in convert [" + sourcePropValue + "] to " + targetPropClass.getName());
    }
}
