package com.example.util;

public class SubStrUtil {

    /**
     * @param str   被截取的字符串
     * @param vchar 获取vchar后几位字符春
     * @param itg   是否携带当前符号
     * @return
     */
    public static String subLastString(String str, String vchar, Integer itg) {
        return str.substring(str.lastIndexOf(vchar) + itg);
    }

    /**
     * @param str   被截取的字符串
     * @param vchar 获取vchar后几位字符春
     * @param itg   是否携带当前符号
     * @return
     */
    public static String subBeginString(String str, String vchar, Integer itg) {

        return str.substring(itg, str.lastIndexOf(vchar));
    }

    /**
     * 截取两个特定字符之间的值
     *
     * @param str    被截取的字符串
     * @param vchar1 获取vchar后几位字符春1
     * @param vchar2 获取vchar前几位字符春2
     * @param itg1   是否携带当前符号1
     * @param itg2   是否携带当前符号2
     * @return
     */
    public static String subBeginAndLastString(String str, String vchar1, String vchar2, Integer itg1, Integer itg2) {
        return SubStrUtil.subBeginString(SubStrUtil.subLastString(str, vchar2, itg2), vchar1, itg1);
    }
}