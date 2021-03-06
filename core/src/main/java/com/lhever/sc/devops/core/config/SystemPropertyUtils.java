package com.lhever.sc.devops.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * <p>
 * 类说明
 * </p>
 *
 * @author lihong10 2019/7/31 10:39
 * @version v1.0
 * @modificationHistory=========================逻辑或功能性重大变更记录
 * @modify by user: {修改人} 2019/7/31 10:39
 * @modify by reason:{方法名}:{原因}
 */
public class SystemPropertyUtils {
    private static final Logger logger = LoggerFactory.getLogger(SystemPropertyUtils.class);

    /**
     * Returns {@code true} if and only if the system property with the specified {@code key}
     * exists.
     */
    public static boolean contains(String key) {
        return get(key) != null;
    }

    /**
     * Returns the value of the Java system property with the specified
     * {@code key}, while falling back to {@code null} if the property com.hikvision.community.health.chep.modules.access fails.
     *
     * @return the property value or {@code null}
     */
    public static String get(String key) {
        return get(key, null);
    }

    /**
     * Returns the value of the Java system property with the specified
     * {@code key}, while falling back to the specified default value if
     * the property com.hikvision.community.health.chep.modules.access fails.
     *
     * @return the property value.
     *         {@code def} if there's no such property or if an com.hikvision.community.health.chep.modules.access to the
     *         specified property is not allowed.
     */
    public static String get(final String key, String def) {
        if (key == null) {
            throw new NullPointerException("key");
        }
        if (key.isEmpty()) {
            throw new IllegalArgumentException("key must not be empty.");
        }

        String value = null;
        try {
            if (System.getSecurityManager() == null) {
                value = System.getProperty(key);
            } else {
                value = AccessController.doPrivileged(new PrivilegedAction<String>() {
                    @Override
                    public String run() {
                        return System.getProperty(key);
                    }
                });
            }
        } catch (SecurityException e) {
            logger.warn("Unable to retrieve a system property '{}'; default values will be used.", key, e);
        }

        if (value == null) {
            return def;
        }

        return value;
    }

    /**
     * Returns the value of the Java system property with the specified
     * {@code key}, while falling back to the specified default value if
     * the property com.hikvision.community.health.chep.modules.access fails.
     *
     * @return the property value.
     *         {@code def} if there's no such property or if an com.hikvision.community.health.chep.modules.access to the
     *         specified property is not allowed.
     */
    public static boolean getBoolean(String key, boolean def) {
        String value = get(key);
        if (value == null) {
            return def;
        }

        value = value.trim().toLowerCase();
        if (value.isEmpty()) {
            return def;
        }

        if ("true".equals(value) || "yes".equals(value) || "1".equals(value)) {
            return true;
        }

        if ("false".equals(value) || "no".equals(value) || "0".equals(value)) {
            return false;
        }

        logger.warn(
                "Unable to parse the boolean system property '{}':{} - using the default value: {}",
                key, value, def
        );

        return def;
    }

    /**
     * Returns the value of the Java system property with the specified
     * {@code key}, while falling back to the specified default value if
     * the property com.hikvision.community.health.chep.modules.access fails.
     *
     * @return the property value.
     *         {@code def} if there's no such property or if an com.hikvision.community.health.chep.modules.access to the
     *         specified property is not allowed.
     */
    public static int getInt(String key, int def) {
        String value = get(key);
        if (value == null) {
            return def;
        }

        value = value.trim();
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            // Ignore
        }

        logger.warn(
                "Unable to parse the integer system property '{}':{} - using the default value: {}",
                key, value, def
        );

        return def;
    }

    /**
     * Returns the value of the Java system property with the specified
     * {@code key}, while falling back to the specified default value if
     * the property com.hikvision.community.health.chep.modules.access fails.
     *
     * @return the property value.
     *         {@code def} if there's no such property or if an com.hikvision.community.health.chep.modules.access to the
     *         specified property is not allowed.
     */
    public static long getLong(String key, long def) {
        String value = get(key);
        if (value == null) {
            return def;
        }

        value = value.trim();
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            // Ignore
        }

        logger.warn(
                "Unable to parse the long integer system property '{}':{} - using the default value: {}",
                key, value, def
        );

        return def;
    }

    private SystemPropertyUtils() {
        // Unused
    }

}
