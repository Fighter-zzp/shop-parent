package com.zzp.shop.common.utils;

import java.util.Arrays;

/**
 * KPM算法工具类
 * <p>
 *  用来优化匹配字符串
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/5 11:07
 * @see  KPMUtils
 **/
public class KPMUtils {

    public static void main(String[] args) {
        String str1 = "BBC ABCDAB ABCDABCDABDE";
        String str2 = "BCDABCDA";
        //String str2 = "BBC";
        int index = kmpSearch(str1, str2);
        System.out.println("index=" + index);
    }

    /**
     * @param str1 源字符串
     * @param str2 子串
     * @return 如果是-1 就是没有匹配到，否则返回第一个匹配的位置
     */
    public static int kmpSearch(String str1, String str2) {
        var next = kmpNext(str2);
        //遍历
        for (int i = 0, j = 0; i < str1.length(); i++) {
            //需要处理 str1.charAt(i) != str2.charAt(j), 去调整 j 的大小
            //KMP 算法核心点, 可以验证...
            while (j > 0 && str1.charAt(i) != str2.charAt(j)) {
                j = next[j - 1];
            }

            if (str1.charAt(i) == str2.charAt(j)) {
                j++;
            }
            //找到了 // j = 3 i
            if (j == str2.length()) {
                return i - j + 1;
            }
        }
        return -1;
    }

    /**
     * 获取到一个字符串(子串) 的部分匹配值表
     * @param dest 目标字符
     * @return 匹配好的索引组
     */
    private static int[] kmpNext(String dest) {
        //创建一个 next 数组保存部分匹配值
        int[] next = new int[dest.length()];
        //如果字符串是长度为 1 部分匹配值就是 0
        next[0] = 0;
        for (int i = 1, j = 0; i < dest.length(); i++) {
            //当 dest.charAt(i) != dest.charAt(j) ，我们需要从 next[j-1]获取新的 j
            //直到我们发现 有 dest.charAt(i) == dest.charAt(j)成立才退出
            //这时 kmp 算法的核心点
            while (j > 0 && dest.charAt(i) != dest.charAt(j)) {
                j = next[j - 1];
            }

            //当 dest.charAt(i) == dest.charAt(j) 满足时，部分匹配值就是+1
            if(dest.charAt(i) == dest.charAt(j)) {
                j++;
            }
            next[i] = j;
        }
        return next;
    }

}
