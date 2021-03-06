package com.github.codehorde.common.bean.translate;

import com.github.codehorde.common.bean.BeanMapper;
import com.github.codehorde.common.bean.support.ClassHelper;
import com.github.codehorde.common.bean.support.PropertyTranslator;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by baomingfeng at 2018-05-02 13:54:09
 */
public class ListTranslator implements PropertyTranslator<List<?>> {

    @Override
    public List<?> translate(Object sourcePropValue, Type targetPropType) {
        if (sourcePropValue instanceof List) {
            List<?> sourceList = (List<?>) sourcePropValue;
            Type componentType = ClassHelper.getCollectionItemType(targetPropType);

            List retList = ClassHelper.instantiate(sourcePropValue.getClass());
            for (Object source : sourceList) {
                Object target = BeanMapper.deepCopyFrom(source, componentType);
                //noinspection unchecked
                retList.add(target);
            }
            return retList;
        }

        /*
            原则上可支持Array, Set --> List<B>
        */

        throw new IllegalArgumentException(getClass().getSimpleName()
                + ": Error in translate [" + sourcePropValue + "] to " + targetPropType.toString());
    }
}
