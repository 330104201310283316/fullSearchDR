package com.example.util;

public class CreateId {
    private final static String str = "1234567890abcdefghijklmnopqrstuvwxyz";
    private final static int pixLen = str.length();
    private static volatile int pixOne = 0;
    private static volatile int pixTwo = 0;
    private static volatile int pixThree = 0;
    private static volatile int pixFour = 0;

    final public synchronized static String generate() {
        StringBuilder sb = new StringBuilder();// 创建一个StringBuilder
        sb.append(Long.toHexString(System.currentTimeMillis()));// 先添加当前时间的毫秒值的16进制
        pixFour++;
        if (pixFour == pixLen) {
            pixFour = 0;
            pixThree++;
            if (pixThree == pixLen) {
                pixThree = 0;
                pixTwo++;
                if (pixTwo == pixLen) {
                    pixTwo = 0;
                    pixOne++;
                    if (pixOne == pixLen) {
                        pixOne = 0;
                    }
                }
            }
        }
        return sb.append(str.charAt(pixOne)).append(str.charAt(pixTwo)).append(str.charAt(pixThree)).append(str.charAt(pixFour)).toString();
    }

}
