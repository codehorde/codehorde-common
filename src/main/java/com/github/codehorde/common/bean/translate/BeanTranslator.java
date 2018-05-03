package com.github.codehorde.common.bean.translate;

import com.github.codehorde.common.bean.BeanCopierUtils;
import com.github.codehorde.common.bean.support.ClassHelper;

/**
 * 普通对象互相复制
 * <p>
 * Created by baomingfeng at 2018-05-02 16:59:01
 */
public class BeanTranslator implements PropertyTranslator<Object> {

    @Override
    public Object convert(Object sourcePropValue, Class targetPropClass,
                          Object context, Object sourceObject, Object targetObject) {
        Object retVal = ClassHelper.instantiate(targetPropClass);
        BeanCopierUtils.adaptMapping(sourcePropValue, retVal);
        return retVal;
    }
}
