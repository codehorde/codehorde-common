package com.github.codehorde.common.bean.translate;

import com.github.codehorde.common.bean.BeanCopierHelper;
import com.github.codehorde.common.bean.support.ClassHelper;
import com.github.codehorde.common.bean.support.PropertyTranslator;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by baomingfeng at 2018-05-02 13:54:09
 */
public class SetTranslator implements PropertyTranslator<Set<?>> {

    @Override
    public Set<?> translate(Object sourcePropValue, Type targetPropType, Object context) {
        if (sourcePropValue instanceof Set) {
            Set<?> sourceList = (Set<?>) sourcePropValue;
            Class<?> componentClass = ClassHelper.getCollectionItemClass(targetPropType);

            HashSet retSet = new HashSet();
            //noinspection Duplicates
            for (Object source : sourceList) {
                if (componentClass == null) {
                    componentClass = source.getClass();
                }
                Object target;
                if (ClassHelper.isBasicClass(componentClass)) {
                    target = source;
                } else {
                    target = BeanCopierHelper.createBean(source, componentClass);
                }
                //noinspection unchecked
                retSet.add(target);
            }
            return retSet;
        }

        /*
            原则上可支持Array, Set --> List<B>
        */

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in translate [" + sourcePropValue + "] to " + targetPropType.toString());
    }
}
