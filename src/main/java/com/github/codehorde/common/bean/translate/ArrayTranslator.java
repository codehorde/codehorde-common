package com.github.codehorde.common.bean.translate;

import com.github.codehorde.common.bean.BeanMapper;
import com.github.codehorde.common.bean.support.ClassHelper;
import com.github.codehorde.common.bean.support.PropertyTranslator;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

/**
 * Created by baomingfeng at 2018-05-02 13:54:09
 */
public class ArrayTranslator implements PropertyTranslator<Object> {

    @Override
    public Object translate(Object sourcePropValue, Type targetPropType) {
        Class<?> sourcePropClass = sourcePropValue.getClass();

        if (sourcePropClass.isArray()) {
            Class targetPropClass = (Class) targetPropType;
            Class componentType = targetPropClass.getComponentType();
            int len = Array.getLength(sourcePropValue);
            Object retArray = Array.newInstance(componentType, len);
            if (ClassHelper.isBasicClass(componentType)) {
                //noinspection SuspiciousSystemArraycopy
                System.arraycopy(sourcePropValue, 0, retArray, 0, len);
            } else {
                for (int index = 0; index < len; index++) {
                    Object source = Array.get(sourcePropValue, index);
                    Object target = BeanMapper.deepCopyFrom(source, componentType);
                    Array.set(retArray, index, target);
                }
            }
            return retArray;
        }

        /*
            目前只支持数组转数组 A[] --> B[]，原则上可支持List, Set --> B[]
        */

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in translate [" + sourcePropValue + "] to " + targetPropType.toString());
    }
}
