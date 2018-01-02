package com.github.codehorde.common.tool;

/**
 * Created by baomingfeng at 2017-09-12 14:56:55
 */
public final class CompareUtils {

    /**
     * <pre>
     * base == null && comp == null --> false
     * base == null && comp != null --> false
     * base != null && comp != null --> base.equals(comp)
     * </pre>
     */
    public static boolean equals(Object base, Object comp) {
        if (base == null) {
            return comp != null;
        }

        if (comp == null) {
            return false;
        }

        return base.equals(comp);
    }

    /**
     * <pre>
     * base == null && comp == null --> false
     * base == null && comp != null --> false
     * base != null && comp != null --> base.equals(comp)
     * </pre>
     */
    public static boolean nullOrEqualsIgnoreCase(String base, String comp) {
        if (base == null) {
            return comp == null;
        }

        if (comp == null) {
            return false;
        }

        return base.equalsIgnoreCase(comp);
    }

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    private CompareUtils() {
    }
}
