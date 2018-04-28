package com.github.codehorde.common.bean;

import com.github.codehorde.common.bean.support.Holder;
import com.github.codehorde.common.bean.translate.*;
import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.core.Converter;

import java.math.BigDecimal;
import java.math.BigInteger;
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
     * 源对象和目标对象属性名称相同但类型不同，目标对象处理为null
     */
    public static void simpleMapping(Object from, Object to) {
        BeanCopier bc = findCopier(from.getClass(), to.getClass(), NULL_CONVERTER);
        bc.copy(from, to, NULL_CONVERTER);//Converter可有可无，BeanCopier没有重载的函数
    }

    /**
     * 不做任何转换，源对象和目标对象属性名称相同但类型不同，
     * 目标对象使用强制转换的结果，可能会报转换异常错误（ClassCastException）
     */
    public static void directMapping(Object from, Object to) {
        BeanCopier bc = findCopier(from.getClass(), to.getClass(), DIRECT_CONVERTER);
        bc.copy(from, to, DIRECT_CONVERTER);
    }

    /**
     * <pre>
     * 如果源对象和目标对象属性名称相同但类型不同，尝试从支持的转换器中转换，参见PropertyTranslator
     * 在此过程中：
     *     1、如果目标的属性类型支持转换（能根据该类型匹配PropertyTranslator），复制使用源对象属性值转换后结果，
     *     转换过程中可能会出现错误，如源属性不能转换目标类型
     *
     *     2、如果目标的属性类型不支持的转换，复制使用源对象对应的属性值，
     *     目标对象使用强制转换的结果，可能会报转换异常错误（ClassCastException）
     * </pre>
     */
    public static void smartMapping(Object from, Object to) {
        BeanCopier bc = findCopier(from.getClass(), to.getClass(), SMART_CONVERTER);
        bc.copy(from, to, SMART_CONVERTER);
    }

    /*
        SourceClass --> Map<TargetClass, Map<Converter, BeanCopier>>
    */
    private static ConcurrentMap<Class<?>, ConcurrentMap<Class<?>, ConcurrentMap<Converter, BeanCopier>>>
            beanCopiers = new ConcurrentHashMap<Class<?>, ConcurrentMap<Class<?>, ConcurrentMap<Converter, BeanCopier>>>();

    private final static Converter NULL_CONVERTER = new SimpleConverter();
    private final static Converter DIRECT_CONVERTER = new DirectConverter();
    private final static Converter SMART_CONVERTER = new SmartConverter();

    public static BeanCopier findCopier(Class<?> sourceClass, Class<?> targetClass) {
        return findCopier(sourceClass, targetClass, DIRECT_CONVERTER);
    }

    public static BeanCopier findCopier(Class<?> sourceClass, Class<?> targetClass, Converter converter) {
        ConcurrentMap<Class<?>, ConcurrentMap<Converter, BeanCopier>> targetCopierMap = beanCopiers.get(sourceClass);
        if (targetCopierMap == null) {
            targetCopierMap = new ConcurrentHashMap<Class<?>, ConcurrentMap<Converter, BeanCopier>>();
            beanCopiers.putIfAbsent(sourceClass, targetCopierMap);
        }
        ConcurrentMap<Converter, BeanCopier> copierMap = targetCopierMap.get(targetClass);
        if (copierMap == null) {
            copierMap = new ConcurrentHashMap<Converter, BeanCopier>();
            targetCopierMap.putIfAbsent(targetClass, copierMap);
        }

        BeanCopier copier = copierMap.get(converter);
        if (copier == null) {
            if (converter == NULL_CONVERTER) {
                copier = BeanCopier.create(sourceClass, targetClass, false);
            } else {
                copier = BeanCopier.create(sourceClass, targetClass, true);
            }
            copierMap.putIfAbsent(converter, copier);
        }
        return copier;
    }

    /*
        SourcePropertyClass --> Map<TargetPropertyClass, PropertyConverter>
    */
    private static ConcurrentMap<Class<?>, PropertyTranslator>
            SupportPropertyTranslatorMap = new ConcurrentHashMap<Class<?>, PropertyTranslator>();

    static {
        SupportPropertyTranslatorMap.put(Enum.class, new EnumTranslator());
        SupportPropertyTranslatorMap.put(String.class, new StringTranslator());
        SupportPropertyTranslatorMap.put(BigInteger.class, new BigIntegerTranslator());
        SupportPropertyTranslatorMap.put(BigDecimal.class, new BigDecimalTranslator());
        SupportPropertyTranslatorMap.put(byte.class, new ByteTranslator());
        SupportPropertyTranslatorMap.put(Byte.class, new ByteTranslator());
        SupportPropertyTranslatorMap.put(double.class, new DoubleTranslator());
        SupportPropertyTranslatorMap.put(Double.class, new DoubleTranslator());
        SupportPropertyTranslatorMap.put(float.class, new FloatTranslator());
        SupportPropertyTranslatorMap.put(Float.class, new FloatTranslator());
        SupportPropertyTranslatorMap.put(int.class, new IntegerTranslator());
        SupportPropertyTranslatorMap.put(Integer.class, new IntegerTranslator());
        SupportPropertyTranslatorMap.put(long.class, new LongTranslator());
        SupportPropertyTranslatorMap.put(Long.class, new LongTranslator());
        SupportPropertyTranslatorMap.put(short.class, new ShortTranslator());
        SupportPropertyTranslatorMap.put(Short.class, new ShortTranslator());
    }

    private static ConcurrentMap<Class<?>, Class<?>>
            PrimaryWrapTypeMap = new ConcurrentHashMap<Class<?>, Class<?>>();

    static {
        PrimaryWrapTypeMap.put(long.class, Long.class);
        PrimaryWrapTypeMap.put(double.class, Double.class);
        PrimaryWrapTypeMap.put(int.class, Integer.class);
        PrimaryWrapTypeMap.put(float.class, Float.class);
        PrimaryWrapTypeMap.put(short.class, Short.class);
        PrimaryWrapTypeMap.put(char.class, Character.class);
        PrimaryWrapTypeMap.put(boolean.class, Boolean.class);
        PrimaryWrapTypeMap.put(byte.class, Byte.class);
        PrimaryWrapTypeMap.put(void.class, Void.class);
    }

    private static ConcurrentMap<Class<?>, Holder<PropertyTranslator>>
            CachePropertyTranslatorMap = new ConcurrentHashMap<Class<?>, Holder<PropertyTranslator>>();

    /**
     * 根据目标的属性类型获取对象的转换器
     */
    private static PropertyTranslator findPropertyTranslator(Class target) {
        Holder<PropertyTranslator> holder = CachePropertyTranslatorMap.get(target);
        if (holder != null) {
            return holder.get();
        } else {
            PropertyTranslator translator = null;

            Class<?> searchType = target;
            while (searchType != null && Object.class != searchType) {
                translator = SupportPropertyTranslatorMap.get(searchType);
                if (translator != null) {
                    break;
                }
                searchType = searchType.getSuperclass();
            }

            holder = new Holder<PropertyTranslator>(translator);
            CachePropertyTranslatorMap.putIfAbsent(target, holder);
            return translator;
        }
    }

    /**
     * 不同类型的属性直接变为null
     */
    private static class SimpleConverter implements Converter {

        @Override
        public Object convert(Object value, Class target, Object context) {
            //noinspection unchecked
            if (target.isAssignableFrom(value.getClass())) {
                return value;
            }
            return null;
        }
    }

    /**
     * 不做任何转换，但不同类型的属性可能会报错
     */
    private static class DirectConverter implements Converter {

        @Override
        public Object convert(Object value, Class target, Object context) {
            return value;
        }
    }

    /**
     * <pre>
     *     补充了一部分自动处理的方式
     *          1：目标对象同名属性类型为String，源对象属性转换
     * </pre>
     */
    @SuppressWarnings("unchecked")
    private static class SmartConverter implements Converter {

        @Override
        public Object convert(Object value, Class target, Object context) {
            if (value == null) {
                return null;
            }

            if (!isCompatible(value, target)) {
                PropertyTranslator propertyTranslator = findPropertyTranslator(target);
                if (propertyTranslator != null) {
                    return propertyTranslator.convert(value, target);
                }
            }

            return value;
        }

        /**
         * <pre>
         * 1、复制源和目标类类型相同
         * 2、目标属性类型为基础类型使用对应的包装类型比较（value为对象类型，不可能为基础类型）
         * </pre>
         */
        private boolean isCompatible(Object value, Class target) {
            return value.getClass() == target
                    || (target.isPrimitive() && PrimaryWrapTypeMap.get(target) == target);
        }
    }
}
