package com.github.codehorde.common.bean.translate;

import com.github.codehorde.common.bean.BeanMapper;
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
    public Map<?, ?> translate(Object sourcePropValue, Type targetPropType) {
        if (sourcePropValue instanceof Map) {
            Map<?, ?> sourceMap = (Map<?, ?>) sourcePropValue;
            Type keyType = null;
            Type valueType = null;


            if (targetPropType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) targetPropType;
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                if (typeArguments.length > 0) {
                    keyType = typeArguments[0];
                }
                if (typeArguments.length > 1) {
                    valueType = typeArguments[1];
                }
            }

            HashMap retMap = ClassHelper.instantiate(sourcePropValue.getClass());
            for (Map.Entry<?, ?> entry : sourceMap.entrySet()) {
                Object targetKey = BeanMapper.deepCopyFrom(entry.getKey(), keyType);
                Object targetValue = BeanMapper.deepCopyFrom(entry.getValue(), valueType);
                //noinspection unchecked
                retMap.put(targetKey, targetValue);
            }
            return retMap;
        }

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in translate [" + sourcePropValue + "] to " + targetPropType.toString());
    }
}
