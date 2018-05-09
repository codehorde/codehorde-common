package com.github.codehorde.common.bean.translate;

import com.github.codehorde.common.bean.BeanMapper;
import com.github.codehorde.common.bean.support.ClassHelper;
import com.github.codehorde.common.bean.support.PropertyTranslator;

import java.lang.reflect.Type;
import java.util.Set;

/**
 * Created by baomingfeng at 2018-05-02 13:54:09
 */
public class SetTranslator implements PropertyTranslator<Set<?>> {

    @Override
    public Set<?> translate(Object sourcePropValue, Type targetPropType) {
        if (sourcePropValue instanceof Set) {
            Set<?> sourceSet = (Set<?>) sourcePropValue;
            Type componentType = ClassHelper.getCollectionItemType(targetPropType);

            Set retSet = ClassHelper.instantiate(sourcePropValue.getClass());
            for (Object source : sourceSet) {
                Object target = BeanMapper.deepCopyFrom(source, componentType);
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
