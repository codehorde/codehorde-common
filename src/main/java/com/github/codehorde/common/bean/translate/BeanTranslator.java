package com.github.codehorde.common.bean.translate;

import com.github.codehorde.common.bean.BeanCopierHelper;
import com.github.codehorde.common.bean.support.ClassHelper;
import com.github.codehorde.common.bean.support.PropertyTranslator;

import java.lang.reflect.Type;

/**
 * 普通对象互相复制
 * <p>
 * Created by baomingfeng at 2018-05-02 16:59:01
 */
public class BeanTranslator implements PropertyTranslator<Object> {

    @Override
    public Object translate(Object sourcePropValue, Type targetPropType, Object context) {
        Class<?> targetClass = ClassHelper.getWrapClass(targetPropType);
        Object target = ClassHelper.instantiate(targetClass);
        BeanCopierHelper.deepClone(sourcePropValue, target);
        return target;
    }
}
