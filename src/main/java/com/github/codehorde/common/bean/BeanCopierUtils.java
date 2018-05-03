package com.github.codehorde.common.bean;

import com.github.codehorde.common.bean.support.DirectConverter;
import com.github.codehorde.common.bean.support.SmartConverter;
import net.sf.cglib.beans.BeanCopier;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 对象复制
 * <p>
 * Created by baomingfeng at 2018-04-28 11:04:59
 */
public final class BeanCopierUtils {

    private BeanCopierUtils() {
    }

    /**
     * Cglib BeanCopier基础的功能，源属性对象和目标属性对象名称相同但类型不同，目标属性处理为null
     */
    public static void simpleMapping(Object from, Object to) {
        BeanCopier bc = findCopier(from.getClass(), to.getClass(), false);
        bc.copy(from, to, null);//Converter可有可无，BeanCopier没有重载的函数
    }

    /**
     * 源属性对象和目标属性对象属性名称相同但类型不同，不做任何转换，
     * 目标属性使用强制转换的结果，转换可能会出现异常错误（ClassCastException）
     */
    public static void directMapping(Object from, Object to) {
        BeanCopier bc = findCopier(from.getClass(), to.getClass(), true);
        bc.copy(from, to, new DirectConverter());
    }

    /**
     * <pre>
     * 如果源属性和目标属性名称相同但类型不同，尝试从支持的转换器中转换，参见PropertyTranslator
     * 在此过程中：
     *     1、如果目标属性类型支持转换（能根据该类型匹配PropertyTranslator），复制使用源对象属性值转换后结果，
     *     如源属性不能转换目标类型，PropertyTranslator处理过程中可能会抛出错误
     *
     *     2、如果目标的属性类型不支持的转换，复制使用源对象对应的属性值，
     *     目标对象使用强制转换的结果，可能会报转换异常错误（ClassCastException）
     * </pre>
     */
    public static void adaptMapping(Object from, Object to) {
        BeanCopier bc = findCopier(from.getClass(), to.getClass(), true);
        bc.copy(from, to, new SmartConverter(from, to));//ThreadLocal隐式传参？
    }

    /*
        SourceClass --> Map<TargetClass, Map<Converter, BeanCopier>>
    */
    private static ConcurrentMap<Class<?>, ConcurrentMap<Class<?>, ConcurrentMap<Boolean, BeanCopier>>>
            beanCopiers = new ConcurrentHashMap<Class<?>, ConcurrentMap<Class<?>, ConcurrentMap<Boolean, BeanCopier>>>();

    public static BeanCopier findCopier(Class<?> sourceClass, Class<?> targetClass) {
        return findCopier(sourceClass, targetClass, false);
    }

    //生成类过程比较耗费资源，使用synchronized加锁处理
    private final static Object CreateClassLock = new Object();

    public static BeanCopier findCopier(Class<?> sourceClass, Class<?> targetClass, boolean useConverter) {
        ConcurrentMap<Class<?>, ConcurrentMap<Boolean, BeanCopier>> targetCopierMap = beanCopiers.get(sourceClass);
        if (targetCopierMap == null) {
            targetCopierMap = new ConcurrentHashMap<Class<?>, ConcurrentMap<Boolean, BeanCopier>>();
            beanCopiers.putIfAbsent(sourceClass, targetCopierMap);
            targetCopierMap = beanCopiers.get(sourceClass);
        }

        ConcurrentMap<Boolean, BeanCopier> copierMap = targetCopierMap.get(targetClass);
        if (copierMap == null) {
            copierMap = new ConcurrentHashMap<Boolean, BeanCopier>();
            targetCopierMap.putIfAbsent(targetClass, copierMap);
            copierMap = targetCopierMap.get(targetClass);
        }

        BeanCopier copier = copierMap.get(useConverter);
        if (copier == null) {
            synchronized (CreateClassLock) {
                copier = copierMap.get(useConverter);
                if (copier == null) {
                    copier = BeanCopier.create(sourceClass, targetClass, useConverter);
                    copierMap.put(useConverter, copier);
                }
            }
        }

        return copier;
    }

}

