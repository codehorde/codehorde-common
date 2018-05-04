package com.github.codehorde.common.bean.translate;

import com.github.codehorde.common.bean.BeanCopierHelper;
import com.github.codehorde.common.bean.support.ClassHelper;
import com.github.codehorde.common.bean.support.PropertyTranslator;
import com.github.codehorde.common.bean.support.SmartConverter;
import net.sf.cglib.beans.BeanCopier;

import java.lang.reflect.Type;

/**
 * 普通对象互相复制
 * <p>
 * Created by baomingfeng at 2018-05-02 16:59:01
 */
public class BeanTranslator implements PropertyTranslator<Object> {

    @Override
    public Object translate(Object sourcePropValue, Type targetPropType, Object context) {
        Class targetPropClass = ClassHelper.getWrapClass(targetPropType);
        BeanCopier copier = BeanCopierHelper.findCopier(sourcePropValue.getClass(), targetPropClass, true);
        Object targetPropObject = ClassHelper.instantiate(targetPropClass);
        copier.copy(sourcePropValue, targetPropObject, new SmartConverter());
        return targetPropObject;
    }
}
