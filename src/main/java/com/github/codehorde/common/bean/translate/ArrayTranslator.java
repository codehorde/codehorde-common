package com.github.codehorde.common.bean.translate;

import com.github.codehorde.common.bean.BeanCopierUtils;
import com.github.codehorde.common.bean.support.ClassHelper;

import java.lang.reflect.Array;

/**
 * Created by baomingfeng at 2018-05-02 13:54:09
 */
public class ArrayTranslator implements PropertyTranslator<Object> {

    @Override
    public Object convert(Object sourcePropValue, Class targetPropClass,
                          Object context, Object sourceObject, Object targetObject) {
        Class<?> sourcePropClass = sourcePropValue.getClass();

        if (sourcePropClass.isArray()) {
            Class componentType = targetPropClass.getComponentType();
            int len = Array.getLength(sourcePropValue);
            Object retArray = Array.newInstance(componentType, len);
            for (int index = 0; index < len; index++) {
                Object from = Array.get(sourcePropValue, index);
                Object to = ClassHelper.instantiate(componentType);
                BeanCopierUtils.adaptMapping(from, to);
                Array.set(retArray, index, to);
            }
            return retArray;
        }

        /*
            目前只支持数组转数组 A[] --> B[]，原则上可支持List, Set --> B[]
        */

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in convert [" + sourcePropValue + "] to " + targetPropClass.getName());
    }
}
