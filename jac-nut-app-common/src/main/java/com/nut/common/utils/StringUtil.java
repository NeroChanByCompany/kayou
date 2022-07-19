package com.nut.common.utils;

import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @Description: String工具类
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.common.utils
 * @Author: yzl
 * @CreateTime: 2021-06-15 10:15
 * @Version: 1.0
 */

public class StringUtil {

    /**
     * 有一个为空则返回空
     *
     * @param strs
     * @return
     */
    public static boolean isEmpty(String... strs) {
        if (strs == null || strs.length == 0) {
            return true;
        }

        for (String str : strs) {
            if (StringUtils.isEmpty(str)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 所有的都为空 才返回true
     *
     * @param strs
     * @return
     */
    public static boolean isAllEmpty(String... strs) {
        if (strs == null || strs.length == 0) {
            return true;
        }

        for (String str : strs) {
            if (!StringUtils.isEmpty(str)) {
                return false;
            }
        }

        return true;
    }

    public static String distinct(String str, String spliter) {
        if (StringUtils.isEmpty(str))
            return str;

        String[] tmp = str.split(spliter);
        Set<String> set = new HashSet<>(Arrays.asList(tmp));
        StringBuilder sb = new StringBuilder();
        for (String s : set) {
            sb.append(s).append(spliter);
        }

        if (sb.length() > 0)
            sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    public static boolean isDigit(String str) {
        if (StringUtils.isEmpty(str))
            return false;

        str = str.trim();

        if (StringUtils.isEmpty(str))
            return false;

        int j = 0;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if ('.' == ch) {
                if (j >= 1)
                    return false;
                j++;
                continue;
            }
            if (!Character.isDigit(ch)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 把数据库的UUID转为UUID
     *
     * @param s
     * @return
     */
    public static String changeUUID(String s) {
        s =
                s.substring(0, 8) + "-" + s.substring(8, 12) + "-" + s.substring(12, 16) + "-" + s.substring(16, 20) + "-"
                        + s.substring(20);
        return s;
    }

    /**
     * 允许null对象的trim方法
     *
     * @param str 给定的字符串
     * @return trim后的字符串
     */
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    /**
     * 判断字符串是否为null对象或是空白字符
     *
     * @param str 给定的字符串
     * @return true：字符串为空
     * @author zhangyu
     */
    public static boolean isEmpty(String str) {
        return ((str == null) || (str.trim().length() == 0));
    }

    /**
     * 判断字符串是否不为null对象或是空白字符
     *
     * @param str 给定的字符串
     * @return true：字符串不为空
     * @author zhangyu
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 对字符串内容为null或""时做统一返回null处理
     *
     * @param value 需要判断的字符串内容
     * @author sunyu
     */
    public static String unifiedEmptyValue(String value) {
        return !isEmpty(value) ? value : null;
    }

    /**
     * 判断两个字符串是否相等(忽略大小写)
     *
     * @param str        字符串1
     * @param compareStr 字符串2
     * @return true：相等， false：不相等
     * @author zhangyu
     */
    public static boolean isEqIgnoreCase(String str, String compareStr) {
        if (str == null) {
            return compareStr == null;
        }
        return str.equalsIgnoreCase(compareStr);
    }

    /**
     * 判断两个字符串是否不相等(忽略大小写)
     *
     * @param str        字符串1
     * @param compareStr 字符串2
     * @return true：不相等， false：相等
     */
    public static boolean isNotEqIgnoreCase(String str, String compareStr) {
        return !isEqIgnoreCase(str, compareStr);
    }

    /**
     * 判断两个字符串是否相等
     *
     * @param str   字符串1
     * @param other 字符串2
     * @return true：相等
     * @author zhangyu
     */
    public static boolean isEq(String str, String other) {
        if (str == null) {
            return other == null;
        }
        return str.equals(other);
    }

    /**
     * 判断两个字符串是否不相等
     *
     * @param str   字符串1
     * @param other 字符串2
     * @return true：不相等
     * @author zhangyu
     */
    public static boolean isNotEq(String str, String other) {
        return !isEq(str, other);
    }

    /**
     * 判断字符串和整数是否在字符串上相等
     *
     * @param str   字符串
     * @param other 整数
     * @return true：相等
     * @author zhangyu
     */
    public static boolean isEq(String str, int other) {
        return String.valueOf(other).equals(str);
    }

    /**
     * 判断字符串和整数是否在字符串上不相等
     *
     * @param str   字符串
     * @param other 整数
     * @return true：不相等
     * @author zhangyu
     */
    public static boolean isNotEq(String str, int other) {
        return !isEq(str, other);
    }

    /**
     * 判断字符串和整数是否在字符串上相等
     *
     * @param i   整数
     * @param str 字符串
     * @return true：相等
     * @author zhangyu
     */
    public static boolean isEq(int i, String str) {
        return String.valueOf(i).equals(str);
    }

    /**
     * 判断字符串和整数是否在字符串上不相等
     *
     * @param i   整数
     * @param str 字符串
     * @return true：不相等
     * @author zhangyu
     */
    public static boolean isNotEq(int i, String str) {
        return !isEq(i, str);
    }

    /**
     * 判断该字符串是否与后面某个整型参数在字符串上相等
     *
     * @param base    字符串
     * @param matched 整型数组
     * @return true：相等
     * @author zhangyu
     */
    public static boolean matchs(String base, int... matched) {
        int b;
        try {
            b = Integer.parseInt(base);
        } catch (Exception e) {
            return false;
        }

        for (int i = 0; i < matched.length; i++) {
            if (b == matched[i]) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断 该整数是否与后面的某个整数是否相等
     *
     * @param base    整数
     * @param matched 整型数组
     * @return true：相等
     * @author zhangyu
     */
    public static boolean matchs(int base, int... matched) {
        for (int i = 0; i < matched.length; i++) {
            if (base == matched[i]) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断字符串是否与其后面某个参数相等
     *
     * @param base    字符串
     * @param matched 字符串数组
     * @return true：相等
     * @author zhangyu
     */
    public static boolean matchs(String base, String... matched) {
        for (int i = 0; i < matched.length; i++) {
            if (matched[i].equals(base)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将boolean型转换为字符串
     *
     * @param b boolean值
     * @return 1：true，0：false
     * @author zhangyu
     */
    public static String valueOf(boolean b) {
        return b ? "true" : "false";
    }

    /**
     * 对象转换为String
     *
     * @param o 对象
     * @return 如果对象为null，则会返回null对象，而不是返回字符串"null"
     * @author zhangyu
     */
    public static String valueOf(Object o) {
        return (o == null) ? null : o.toString();
    }

    /**
     * 将int型转换为字符串
     *
     * @param i int值
     * @return 字符串
     * @author zhangyu
     */
    public static String valueOf(int i) {
        return String.valueOf(i);
    }

    /**
     * 对象转字符串，若对象为空，返回默认值
     *
     * @param o   对象
     * @param def 默认值
     * @return
     * @author zhangyu
     */
    public static String valueOf(Object o, String def) {
        return (o == null) ? def : o.toString();
    }

    /**
     * 将long型转换为字符串
     *
     * @param l long值
     * @return 字符串
     * @author zhangyu
     */
    public static String valueOf(long l) {
        return String.valueOf(l);
    }

    /**
     * 将double型转换为字符串
     *
     * @param d double值
     * @return 字符串
     * @author zhangyu
     */
    public static String valueOf(double d) {
        return String.valueOf(d);
    }

    /**
     * 判断一个字符串是否全部是整数
     *
     * @param str 字符串
     * @return true：是整数
     */
    public static boolean isNumber(String str) {
        if (str != null) {
            return str.matches("-?\\d+");
        } else {
            return false;
        }
    }

    /**
     * 将String类型转换为Int类型,当转换失败时返回默认值
     *
     * @param str 待转换的字符串
     * @param def 默认值
     * @return
     * @author zhangyu
     */
    public static int toInt(String str, int def) {
        int result = def;
        try {
            result = Integer.parseInt(str);
        } catch (Exception e) {
            return result;
        }
        return result;
    }

    /**
     * 将String类型转换为Long类型,当转换失败时返回默认值
     *
     * @param str 待转换的字符串
     * @param def 默认值
     * @return
     * @author zhangyu
     */
    public static Long toLong(String str, Long def) {
        Long result = def;
        try {
            result = Long.parseLong(str);
        } catch (Exception e) {
            return result;
        }
        return result;
    }

    /**
     * 将String类型转换为Double类型,当转换失败时返回默认值
     *
     * @param str 待转换的字符串
     * @param def 默认值
     * @return
     * @author zhangyu
     */
    public static double toDouble(String str, double def) {
        double result = def;
        try {
            result = Double.parseDouble(str);
        } catch (Exception e) {
            return result;
        }
        return result;
    }

    /**
     * 将String类型转换为Double类型,当转换失败时返回默认值
     *
     * @param doubleNum 待转换的double
     * @param def       默认值
     * @return
     * @author zhangyu
     */
    public static Float doubletoFloat(double doubleNum, Float def) {
        Float result = def;
        try {
            result = (float) doubleNum;
        } catch (Exception e) {
            return result;
        }
        return result;
    }

    /**
     * 字符串转换为整形
     *
     * @param str 字符串
     * @return 整型值
     * @author zhangyu
     */
    public static Integer toInteger(String str) {
        try {
            if (str == null) {
                return null;
            }

            DecimalFormat dcmFmt = new DecimalFormat("#0");
            String strDouble = dcmFmt.format(Double.parseDouble(str));
            return Integer.parseInt(strDouble);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 截取一段字符的长度,不区分中英文,如果数字不正好，则少取一个字符位
     *
     * @param origin 原始字符串
     * @param len    截取长度(一个汉字长度按2算的)
     * @return 返回的字符串
     * @author zhangyu
     */
    public static String subChinseseStr(String origin, int len) {
        if (origin == null || origin.equals("") || len < 1)
            return "";
        byte[] strByte = new byte[len];
        if (len > length(origin)) {
            return origin;
        }

        try {
            System.arraycopy(origin.getBytes("GBK"), 0, strByte, 0, len);
            int count = 0;
            for (int i = 0; i < len; i++) {
                int value = (int) strByte[i];
                if (value < 0) {
                    count++;
                }
            }
            if (count % 2 != 0) {
                len = (len == 1) ? ++len : --len;
            }
            return new String(strByte, 0, len, "GBK");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    /**
     * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为2,英文字符长度为1
     *
     * @param s 需要得到长度的字符串
     * @return 得到的字符串长度
     */
    public static int length(String s) {
        if (s == null)
            return 0;
        char[] c = s.toCharArray();
        int len = 0;
        for (int i = 0; i < c.length; i++) {
            len++;
            if (!isLetter(c[i])) {
                len++;
            }
        }
        return len;
    }

    /**
     * 计算字符串的字符长度（中文、英文均按一个字符计算） 如："中文abc" 长度为 5
     *
     * @param s 字符串
     * @return 返回的字符串长度
     * @author zhangyu
     */
    public static int lengthc(String s) {
        if (s == null) {
            return 0;
        }
        return s.length();
    }

    /**
     * 判断一个字符是Ascill字符还是其它字符（如汉，日，韩文字符）
     *
     * @param c 指定的字符
     * @return true:Ascill字符
     * @author zhangyu
     */
    public static boolean isLetter(char c) {
        int k = 0x80;
        return c / k == 0 ? true : false;
    }

    /**
     * 计算字符串中中文的个数
     *
     * @param s 指定的字符
     * @return 中文的个数
     * @author sunyu
     */
    public static int findNotAscill(String s) {
        if (isEmpty(s)) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (isChinese(s.substring(i, i + 1))) {
                count++;
            }
        }
        return count;
    }

    /**
     * 判断一个字符是不是中文
     *
     * @param c 指定的字符
     * @return true:Ascill字符
     * @author sunyu
     */
    public static boolean isChinese(String c) {
        Pattern pattern = Pattern.compile(RegexpUtils.CHINESE_VAR);
        Matcher matcher = pattern.matcher(c);
        return matcher.matches();
    }

    /**
     * 分隔字符串
     *
     * @param str   被分隔的字符串
     * @param regex 分隔符
     * @return 分隔得到的数组
     * @author zhangyu
     */
    public static String[] split(String str, String regex) {
        if (str == null) {
            return new String[]{""};
        }
        if (regex == null) {
            return new String[]{str};
        }

        return str.split(regex);
    }

    /**
     * 判断输入的字符串是否是可以转换为boolean类型的true/false
     *
     * @param str 给定的字符串
     * @return true：可以转化成boolean
     * @author zhangyu
     */
    public static boolean isBooleanType(String str) {
        String tmpStr = trim(str);
        if (null == tmpStr) {
            return false;
        } else if (tmpStr.equalsIgnoreCase("true") || tmpStr.equalsIgnoreCase("false")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断字符串是否以标点符号结尾（句号、逗号、分号）
     *
     * @param str 给定的字符串
     * @return true：以标点符号结尾
     * @author zhangyu
     */
    public static boolean endWithPunctuation(String str) {
        if (isEmpty(str)) {
            return false;
        }

        return str.endsWith("。") || str.endsWith(".") || str.endsWith("，") || str.endsWith(",") || str.endsWith("；")
                || str.endsWith(";") || str.endsWith("!") || str.endsWith("！");
    }

    /**
     * 判断字符串是否包含字符串
     *
     * @param str  字符串1
     * @param mark 字符串2
     * @return true： 字符串1包含字符串2
     * @author zhangyu
     */
    public static boolean containMark(String str, String mark) {
        if (isEmpty(str)) {
            return false;
        }

        return str.contains(mark);
    }

    /**
     * 子字符串是否包含在字符串列表中
     *
     * @param str     字符串
     * @param strList 字符串列表
     * @return true：包含
     */
    public static boolean containSubStr(String str, String[] strList) {
        boolean containSub = false;
        if (strList.length == 0 || isEmpty(str)) {
            containSub = false;
        }
        for (int i = 0; i < strList.length; i++) {

            if (!strList[i].isEmpty()) {
                if (str.startsWith(strList[i])) {
                    containSub = true;
                }
            }

        }

        return containSub;
    }

    /**
     * 替换“+”和空格
     *
     * @param str 元字符串
     * @return 替换后的字符串
     * @author zhangyu
     */
    public static String replaceStr(String str) {
        if (null == str) {
            return "";
        }
        Pattern p = Pattern.compile("\\+|\\s*");

        Matcher m = p.matcher(str);

        String after = m.replaceAll("");

        return after;
    }

    /**
     * 近似匹配，找到比数组中小的，最接近输入值的数
     *
     * @param source
     * @param matched
     * @return
     * @author zhangyu
     */
    public static int match(int source, int... matched) {
        int x = 0;
        int target = 0;
        for (int i = 0; i < matched.length; i++) {
            if (source >= matched[i]) {
                x = matched[i] - source;

                if (Math.abs(x) < Math.abs(target - source)) {
                    target = matched[i];
                }
            }
        }
        return target;
    }

    /**
     * 用指定的分隔符号拆分字符串
     *
     * @param srcStr 待拆分的字符串
     * @param sep    分隔符号
     * @return 返回拆分后的字符串列表
     * @author zhangyu
     */
    public static List<String> splitString(String srcStr, String sep) {
        List<String> list = new ArrayList<String>();
        if (!isEmpty(srcStr)) {
            String[] temp = srcStr.split(sep);
            for (int i = 0; i < temp.length; i++) {
                list.add(temp[i]);
            }
        }
        return list;
    }

    /**
     * 将列表用指定的符号组装成String
     *
     * @param source    待组装的列表
     * @param seperator 分隔符
     * @return 组装后的字符串
     * @author zhangyu
     */
    public static String parseListToStrBySeperator(List<String> source, String seperator) {
        String result = "";
        // 参数检查
        if (isEmpty(source)) {
            return result;
        }
        // 执行组装
        for (int i = 0; i < source.size(); i++) {
            String str = source.get(i);

            if (i == source.size() - 1) {
                result += str;
            } else {
                result += str + seperator;
            }
        }
        return result;
    }

    /**
     * 验证集合是否为null或为空集
     *
     * @param c 需要判断的集合
     * @return true：null或为空集
     * @author zhangyu
     */
    public static boolean isEmpty(Collection<?> c) {
        return (c == null) || c.isEmpty();
    }

    /**
     * 验证Map是否为null或为空集
     *
     * @param m 需要判断的Map
     * @return true：null或为空集
     * @author zhangyu
     */
    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Map m) {
        return (m == null) || m.isEmpty();
    }

    /**
     * 验证集合是否为null或为空集
     *
     * @param leftStr 需要判断的集合
     * @return true：null或为空集
     * @author zhangyu
     */
    public static String joint(String leftStr, String... rightStr) {
        StringBuffer sb = new StringBuffer();

        sb.append(leftStr);

        for (String str : rightStr) {
            sb.append(str);
        }

        return sb.toString();
    }

    /**
     * 对null对象进行赋值
     *
     * @param o 对象
     * @return String
     * @author zhangyu
     */
    public static String nvl(Object o) {
        return (null == o) ? "" : o.toString().trim();
    }

    /**
     * 校验字符串是否为数字和英文逗号组合
     *
     * @param str supportPayType
     * @return true：数字或者数字加逗号加数字
     * @author zhangyu
     */
    public static boolean reg(String str) {
        String regex = "(\\d+(,\\d+)?)";
        return str.matches(regex);
    }

    /**
     * 判断字符串是否为null
     *
     * @param str
     * @return
     */
    public static boolean isNull(String str) {
        return (str == null);
    }

    /**
     * 获取32位UUID
     *
     * @return
     */
    public static String getUUID() {
        String s = UUID.randomUUID().toString();
        // 去掉“-”符号
        return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18) + s.substring(19, 23) + s.substring(24);
    }

    /**
     * base64加密，可用于生成token
     *
     * @return
     */
    public static String getBase64Str(String oldStr) {
        // return new sun.misc.BASE64Encoder().encode(oldStr.getBytes());
        return new String(Base64.getEncoder().encode(oldStr.getBytes()));
    }

    /**
     * 移除 字符串str 中最后一个字符
     *
     * @param str
     * @param lastStr
     * @return 返回截取后的字符
     */
    public static String removeLastStr(String str, String lastStr) {
        if (isNotEmpty(str) && isNotEmpty(lastStr)) {
            if (str.indexOf(lastStr) != -1) {
                str = str.substring(0, str.lastIndexOf(lastStr));
            }
        }
        return str;
    }

    /**
     * 左补位，右对齐
     *
     * @param oriStr 原字符串
     * @param len    目标字符串长度
     * @param alexin 补位字符
     * @return 目标字符串
     * @author zhangyu
     */
    public static String padLeft(String oriStr, int len, char alexin) {
        String str = "";

        int strlen = oriStr.length();
        if (strlen < len) {
            for (int i = 0; i < len - strlen; i++) {
                str = str + alexin;
            }
        }
        str = oriStr + str;
        return str;
    }

    /**
     * 右补位，左对齐
     *
     * @param oriStr 原字符串
     * @param len    目标字符串长度
     * @param alexin 补位字符
     * @return 目标字符串
     * @author zhangyu
     */
    public static String padRight(String oriStr, int len, char alexin) {
        String str = "";

        int strlen = oriStr.length();
        if (strlen < len) {
            for (int i = 0; i < len - strlen; i++) {
                str = str + alexin;
            }
        }
        str = str + oriStr;
        return str;
    }

    /**
     * String转List
     *
     * @param strList
     * @param spliter
     * @return 目标字符串
     * @author zhangyu
     */
    public static List<String> stringToList(String strList, String spliter) {
        String[] arr = strList.split(spliter);
        List<String> list = Arrays.asList(arr);
        return list;
    }

    /**
     * 字符串拼接
     *
     * @param str
     * @return
     * @author wujiangbo
     */
    public static String concat(String... str) {
        return str == null ? "" : Stream.of(str).filter(Objects::nonNull).reduce((a, b) -> a + b).orElse("");
    }

    public static void main(String[] args) {
        System.out.println(padRight("1", 4, '0'));
    }
}
