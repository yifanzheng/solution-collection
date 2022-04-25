package top.yifan.masterha.util;

import java.util.UUID;

/**
 * UUIDUtil
 *
 * @author star
 */
public class IDUtil {

    private IDUtil() {

    }

    public static String[] chars = new String[]{"a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};


    public static String generateUUID() {
        String ticket = UUID.randomUUID().toString();
        return ticket.replaceAll("-", "");
    }

    /**
     * 生成长度是8位的随机ID
     */
    public static String generateShortUUID() {
        // 调用Java提供的生成随机字符串的对象：32位，十六进制，中间包含-
        String uuid = generateUUID();
        StringBuilder shortBuffer = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = i << 2;
            String str = uuid.substring(index, index + 4);
            int x = Integer.parseInt(str, 16);
            // 用该16进制数取模62（十六进制表示为314（14即E）），结果作为索引取出字符
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString().toLowerCase();
    }
}
