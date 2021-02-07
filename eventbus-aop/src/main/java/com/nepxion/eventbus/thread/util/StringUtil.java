package com.nepxion.eventbus.thread.util;

public final class StringUtil {

    private StringUtil() {

    }

    /**
     * 将字符串第一个字母转换为大写
     *
     * @param value
     * @return
     */
    public static String firstLetterToUpper(String value) {
        char character = Character.toUpperCase(value.charAt(0));
        return Character.toString(character).concat(value.substring(1));
    }

    /**
     * 将字符串第一个字母转换为小写
     *
     * @param value
     * @return
     */
    public static String firstLetterToLower(String value) {
        char character = Character.toLowerCase(value.charAt(0));
        return Character.toString(character).concat(value.substring(1));
    }
}