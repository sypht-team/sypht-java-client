/*
 * PropertyHelper
 */
package com.sypht;

/**
 * @author Simon Mittag
 */
public class PropertyHelper {

    public static String getEnvOrProperty(String key) {
        String value = System.getenv(key);
        if(value==null) {
            value = System.getProperty(key);
        }
        return value;
    }
}
