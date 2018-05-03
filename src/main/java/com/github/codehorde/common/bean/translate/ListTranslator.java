package com.github.codehorde.common.bean.translate;

import com.github.codehorde.common.bean.BeanCopierUtils;
import com.github.codehorde.common.bean.support.ClassHelper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by baomingfeng at 2018-05-02 13:54:09
 */
public class ListTranslator implements PropertyTranslator<List<?>> {

    @Override
    public List<?> convert(Object sourcePropValue, Class targetPropClass,
                           Object context, Object sourceObject, Object targetObject) {

        if (sourcePropValue instanceof List) {
            List<?> sourceList = (List<?>) sourcePropValue;
            Class<?> componentType = null;

            ParameterizedType parameterizedType = ClassHelper.getMethodParameterType(
                    targetObject.getClass(), (String) context, targetPropClass);
            if (parameterizedType != null) {
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                if (typeArguments.length > 0) {
                    Type type = typeArguments[0];
                    //noinspection ConstantConditions
                    componentType = type instanceof Class ? (Class<?>) type : null;
                }
            }
            ArrayList retList = new ArrayList();
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
                retList.add(to);
            }
            return retList;
        }

        /*
            原则上可支持Array, Set --> List<B>
        */

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in convert [" + sourcePropValue + "] to " + targetPropClass.getName());
    }
}
