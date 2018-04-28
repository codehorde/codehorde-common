package com.github.codehorde.common.bean;

import org.apache.commons.beanutils.BeanUtilsBean;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by baomingfeng at 2018-04-27 17:18:49
 */
public final class CommonBeanUtils {

    private final static BeanUtilsBean nonNullAwareInstance;

    static {
        nonNullAwareInstance = new BeanUtilsBean() {
            @Override
            public void copyProperty(Object dest, String name, Object value)
                    throws IllegalAccessException, InvocationTargetException {
                if (value == null) {
                    return;
                }
                super.copyProperty(dest, name, value);
            }
        };
    }

    /**
     * srcObj中null属性不做覆盖
     */
    public static void nonNullOverride(Object srcObj, Object destObj) {
        try {
            nonNullAwareInstance.copyProperties(destObj, srcObj);
        } catch (Exception ex) {
            throw new UnsupportedOperationException(ex);
        }
    }
}