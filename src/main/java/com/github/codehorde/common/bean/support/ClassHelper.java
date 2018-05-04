package com.github.codehorde.common.bean.support;

import net.sf.cglib.reflect.FastClass;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by baomingfeng at 2018-05-02 16:00:24
 */
public final class ClassHelper {

    private static ConcurrentMap<Class<?>, Class<?>>
            PrimaryWrapTypeMap = new ConcurrentHashMap<>();

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

    public static Class<?> mapWrappedClass(Class<?> primaryClass) {
        return PrimaryWrapTypeMap.get(primaryClass);
    }

    private static final CopyOnWriteArraySet<Class<?>>
            BasicClasses = new CopyOnWriteArraySet<>();

    static {
        BasicClasses.addAll(
                Arrays.asList(Long.class, Double.class, Integer.class, Short.class,
                        Character.class, Boolean.class, Void.class,
                        Date.class, String.class, BigDecimal.class, BigInteger.class)
        );
    }

    public static boolean isBasicClass(Class<?> clazz) {
        return BasicClasses.contains(clazz);
    }

    /*
        targetPropClass --> Map<sourcePropClass, Boolean>
    */
    private static ConcurrentMap<Class, ConcurrentMap<Class, Boolean>>
            CompatibleClassCache = new ConcurrentHashMap<>();

    /**
     * <pre>
     *      复制源和目标类类型是否兼容，如果兼容直接使用目标的属性值
     *      满足兼容条件通常：源属性类型需要为基础类型（如int, double）或者基础支持类型（如String, Date, Long）
     * </pre>
     */
    public static boolean matchCompatible(Class<?> sourcePropClass, Class targetPropClass) {
        ConcurrentMap<Class, Boolean> targetClassMap = CompatibleClassCache.get(targetPropClass);
        if (targetClassMap == null) {
            targetClassMap = new ConcurrentHashMap<>();
            CompatibleClassCache.putIfAbsent(targetPropClass, targetClassMap);
            targetClassMap = CompatibleClassCache.get(targetPropClass);
        }
        Boolean compatible = targetClassMap.get(sourcePropClass);

        if (compatible == null) {//缓存不存在
                /*
                    1、如果targetPropClass为基础类型，如int：setXxx(int xxx)
                    当源对象属性 Xxx getXxx()
                       -- Xxx为基础类型的包装类型Integer时，则两者类型兼容
                       -- Xxx不是基础类型的包装类型Integer时，如Xxx.class -> Long，则两者类型不兼容，需要经过IntegerTranslator
                */
            if (targetPropClass.isPrimitive()) {
                compatible = ClassHelper.mapWrappedClass(targetPropClass) == targetPropClass;
            }
                /*
                    2、如果源属性类型sourcePropClass为不可变类型，sourcePropClass是targetPropClass的一个子类，返回兼容
                     -- 如源属性Long getXxx()，目标属性 setXxx(Long xxx)，直接使用源属性的值
                         目标属性 setXxx(Long xxx)，直接使用源属性的值
                */
            else if (ClassHelper.isBasicClass(sourcePropClass)) {
                //noinspection unchecked
                compatible = targetPropClass.isAssignableFrom(sourcePropClass);
            } else {
                /*
                    3、其他情况不兼容
                */
                compatible = false;
            }

            targetClassMap.putIfAbsent(sourcePropClass, compatible);
        }

        return compatible;
    }

    /*
        <targetClass, <methodName, <parameterClass, ParameterizedType>>>
    */
    private static ConcurrentMap<Class<?>, ConcurrentMap<String, ConcurrentMap<Class<?>, Holder<ParameterizedType>>>>
            MethodParameterTypeCache = new ConcurrentHashMap<>();

    /**
     * 返回某个属性的方法的反省参数
     * <p>
     * 属性方法只有一个参数，如方法public void setSkus(List<ItemSkuDo> skus)
     */
    public static ParameterizedType getMethodParameterType(
            Class targetClass, String methodName, Class<?> parameterClass) {
        ConcurrentMap<String, ConcurrentMap<Class<?>, Holder<ParameterizedType>>>
                classMethodTypeMap = MethodParameterTypeCache.get(targetClass);
        if (classMethodTypeMap == null) {
            classMethodTypeMap = new ConcurrentHashMap<>();
            MethodParameterTypeCache.putIfAbsent(targetClass, classMethodTypeMap);
            classMethodTypeMap = MethodParameterTypeCache.get(targetClass);
        }

        ConcurrentMap<Class<?>, Holder<ParameterizedType>>
                methodTypeMap = classMethodTypeMap.get(methodName);
        if (methodTypeMap == null) {
            methodTypeMap = new ConcurrentHashMap<>();
            classMethodTypeMap.putIfAbsent(methodName, methodTypeMap);
            methodTypeMap = classMethodTypeMap.get(methodName);
        }

        Holder<ParameterizedType> typeHolder = methodTypeMap.get(parameterClass);
        if (typeHolder == null) {
            ParameterizedType parameterizedType = null;
            Method method = findMethod(targetClass, methodName, parameterClass);
            if (method != null) {
                Type[] parameterTypes = method.getGenericParameterTypes();
                Type type = parameterTypes[0];
                if (type instanceof ParameterizedType) {
                    parameterizedType = (ParameterizedType) type;
                }
            }

            typeHolder = new Holder<>(parameterizedType);
            methodTypeMap.putIfAbsent(parameterClass, typeHolder);//cache
        }

        return typeHolder.get();
    }

    public static Method findMethod(Class<?> clazz, String name, Class<?>... paramTypes) {
        Class<?> searchType = clazz;
        while (searchType != null) {
            Method[] methods = (searchType.isInterface() ? searchType.getMethods() : getDeclaredMethods(searchType));
            for (Method method : methods) {
                if (name.equals(method.getName()) &&
                        (paramTypes == null || Arrays.equals(paramTypes, method.getParameterTypes()))) {
                    return method;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    /**
     * Cache for {@link Class#getDeclaredMethods()}, allowing for fast resolution.
     */
    private static final ConcurrentMap<Class<?>, Method[]> declaredMethodsCache =
            new ConcurrentHashMap<>(256);

    private static Method[] getDeclaredMethods(Class<?> clazz) {
        Method[] result = declaredMethodsCache.get(clazz);
        if (result == null) {
            result = clazz.getDeclaredMethods();
            declaredMethodsCache.putIfAbsent(clazz, result);
            result = declaredMethodsCache.get(clazz);
        }
        return result;
    }

    public static Class<?> getCollectionItemClass(Type fieldType) {
        if (fieldType instanceof ParameterizedType) {
            Type actualTypeArgument = ((ParameterizedType) fieldType).getActualTypeArguments()[0];
            return getWrapClass(actualTypeArgument);
        }

        return null;
    }

    public static Class<?> getWrapClass(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType itemType = (ParameterizedType) type;
            return (Class<?>) itemType.getRawType();//List<X<Y>>, Set<X<Y>>
        } else if (type instanceof WildcardType) {//List<X extends Y>, List<X super Y> ???
            return null;
        }

        return null;
    }

    /**
     * 留意这里返回的对象不是反射调用构造方法创建出来的（Cglib FastClass）
     */
    public static <T> T instantiate(Class<?> clazz) {
        if (List.class.isAssignableFrom(clazz)) {
            return (T) new ArrayList<>();
        } else if (Set.class.isAssignableFrom(clazz)) {
            return (T) new HashSet<>();
        } else if (Map.class.isAssignableFrom(clazz)) {
            return (T) new HashMap<>();
        }

        FastClass fastClass = getFastClass(clazz);

        try {
            //noinspection unchecked
            return (T) fastClass.newInstance();
        } catch (InvocationTargetException ex) {
            throw new UnsupportedOperationException(
                    "create class [" + clazz + "] instance error! ", ex);
        }
    }

    private static final ConcurrentMap<Class<?>, FastClass>
            FastClassCache = new ConcurrentHashMap<>();

    //生成类过程比较耗费资源，使用synchronized加锁处理
    private final static Object CreateClassLock = new Object();

    public static FastClass getFastClass(Class<?> clazz) {
        FastClass result = FastClassCache.get(clazz);

        if (result == null) {
            synchronized (CreateClassLock) {
                result = FastClassCache.get(clazz);
                if (result == null) {
                    result = FastClass.create(clazz);
                    FastClassCache.put(clazz, result);
                }
            }
        }

        return result;
    }

    private ClassHelper() {
    }
}
