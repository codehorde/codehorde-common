package com.github.codehorde.common.bean.translate;

import com.github.codehorde.common.bean.BeanCopierUtils;
import com.github.codehorde.common.bean.support.ClassHelper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by baomingfeng at 2018-05-02 13:54:09
 */
public class SetTranslator implements PropertyTranslator<Set<?>> {

    @Override
    public Set<?> convert(Object sourcePropValue, Class targetPropClass,
                          Object context, Object sourceObject, Object targetObject) {

        if (sourcePropValue instanceof Set) {
            Set<?> sourceList = (Set<?>) sourcePropValue;
            Class<?> componentType = null;

            ParameterizedType parameterizedType = ClassHelper.getMethodParameterType(
                    targetObject.getClass(), (String) context, targetPropClass);
            if (parameterizedType != null) {
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                Type type = typeArguments[0];
                //noinspection ConstantConditions
                componentType = type instanceof Class ? (Class<?>) type : null;
            }

            HashSet retSet = new HashSet();
            //noinspection Duplicates
            for (Object source : sourceList) {
                if (componentType == null) {
                    componentType = source.getClass();
                }
                Object to;
                if (ClassHelper.isBasicClass(componentType)) {
                    to = source;
                } else {
                    to = ClassHelper.instantiate(componentType);
                    BeanCopierUtils.adaptMapping(source, to);
                }
                //noinspection unchecked
                retSet.add(to);
            }
            return retSet;
        }

        /*
            原则上可支持Array, Set --> List<B>
        */

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in convert [" + sourcePropValue + "] to " + targetPropClass.getName());
    }
}
