package com.github.codehorde.common.bean;

import com.github.codehorde.common.bean.support.ClassHelper;
import com.github.codehorde.common.bean.support.DirectConverter;
import com.github.codehorde.common.bean.support.PropertyTranslator;
import com.github.codehorde.common.bean.support.TranslatorRegistry;
import net.sf.cglib.beans.BeanCopier;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 对象复制
 * <p>
 * Created by baomingfeng at 2018-04-28 11:04:59
 */
public final class BeanCopierHelper {

    private BeanCopierHelper() {
    }

    /**
     * Cglib BeanCopier基础的功能，源属性对象和目标属性对象名称相同但类型不同，目标属性处理为null
     */
    public static void simpleCopy(Object source, Object to) {
        BeanCopier copier = findCopier(source.getClass(), to.getClass());
        copier.copy(source, to, null);//Converter可有可无，BeanCopier没有重载的函数
    }

    public static <T> T simpleCopy(Object source, Class<T> targetClass) {
        BeanCopier copier = findCopier(source.getClass(), targetClass.getClass());
        T target = ClassHelper.instantiate(targetClass);
        copier.copy(source, target, null);
        return target;
    }

    /**
     * 源属性对象和目标属性对象属性名称相同但类型不同，不做任何转换，
     * 目标属性使用强制转换的结果，转换可能会出现异常错误（ClassCastException）
     */
    public static void directCopy(Object source, Object target) {
        BeanCopier copier = findCopier(source.getClass(), target.getClass(), true);
        copier.copy(source, target, new DirectConverter());
    }

    public static <T> T directCopy(Object source, Class<T> targetClass) {
        BeanCopier copier = findCopier(source.getClass(), targetClass, true);
        T target = ClassHelper.instantiate(targetClass);
        copier.copy(source, target, new DirectConverter());
        return target;
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
    public static <T> T createBean(Object source, Class<T> targetClass) {
        PropertyTranslator translator = TranslatorRegistry.findPropertyTranslator(targetClass);
        //noinspection unchecked
        return (T) translator.translate(source, targetClass, null);
    }

    /*
        <SourceClass, <TargetClass, <Converter, BeanCopier>>>
    */
    private static ConcurrentMap<Class<?>, ConcurrentMap<Class<?>, ConcurrentMap<Boolean, BeanCopier>>>
            beanCopiers = new ConcurrentHashMap<>();

    public static BeanCopier findCopier(Class<?> sourceClass, Class<?> targetClass) {
        return findCopier(sourceClass, targetClass, false);
    }

    //生成类过程比较耗费资源，使用synchronized加锁处理
    private final static Object CreateClassLock = new Object();

    public static BeanCopier findCopier(Class<?> sourceClass, Class<?> targetClass, boolean useConverter) {
        ConcurrentMap<Class<?>, ConcurrentMap<Boolean, BeanCopier>> targetCopierMap = beanCopiers.get(sourceClass);
        if (targetCopierMap == null) {
            targetCopierMap = new ConcurrentHashMap<>();
            beanCopiers.putIfAbsent(sourceClass, targetCopierMap);
            targetCopierMap = beanCopiers.get(sourceClass);
        }

        ConcurrentMap<Boolean, BeanCopier> copierMap = targetCopierMap.get(targetClass);
        if (copierMap == null) {
            copierMap = new ConcurrentHashMap<>();
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

