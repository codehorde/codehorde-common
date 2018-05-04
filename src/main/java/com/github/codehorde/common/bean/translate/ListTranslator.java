package com.github.codehorde.common.bean.translate;

import com.github.codehorde.common.bean.BeanCopierHelper;
import com.github.codehorde.common.bean.support.ClassHelper;
import com.github.codehorde.common.bean.support.PropertyTranslator;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by baomingfeng at 2018-05-02 13:54:09
 */
public class ListTranslator implements PropertyTranslator<List<?>> {

    @Override
    public List<?> translate(Object sourcePropValue, Type targetPropType, Object context) {
        if (sourcePropValue instanceof List) {
            List<?> sourceList = (List<?>) sourcePropValue;
            Class<?> componentClass = ClassHelper.getCollectionItemClass(targetPropType);

            ArrayList retList = new ArrayList();
            //noinspection Duplicates
            for (Object source : sourceList) {
                if (componentClass == null) {
                    componentClass = source.getClass();
                }
                Object target;
                if (ClassHelper.isBasicClass(componentClass)) {
                    target = source;
                } else {
                    target = BeanCopierHelper.deepClone(source, componentClass);
                }
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
