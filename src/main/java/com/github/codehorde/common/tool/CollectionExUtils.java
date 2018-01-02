package com.github.codehorde.common.tool;

import java.util.Collection;

/**
 * 命名区分于CollectionUtils，0 0？
 * <p>
 * Created by baomingfeng at 2017-12-06 19:15:41
 */
public final class CollectionExUtils {

    /**
     * 集合元素数量，null --> 0
     */
    public static int sizeOf(Collection<?> collection) {
        return collection == null ? 0 : collection.size();
    }

    /**
     * 返回集合是否包含某些元素
     */
    public static boolean containsIgnoreCase(Collection<String> collection, String str) {
        if (collection == null || collection.isEmpty()) {
            return false;
        }
        for (String elem : collection) {
            if (CompareUtils.nullOrEqualsIgnoreCase(elem, str)) {
                return true;
            }
        }
        return false;
    }

    private CollectionExUtils() {
    }
}
