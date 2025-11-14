package com.example.develop.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

// 新增同音字扩展处理
public class PinyinUtils {
    private static final HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

    static {
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }

    // 支持同音字转换（核心方法）
    public static String toFuzzyPinyin(String chinese) {
        try {
            StringBuilder result = new StringBuilder();
            for (char c : chinese.toCharArray()) {
                String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(c, format);
                if (pinyins != null && pinyins.length > 0) {
                    // 同音字处理：取第一个发音（可扩展多音字逻辑）
                    result.append(pinyins[0]);
                } else {
                    result.append(c);
                }
            }
            return result.toString();
        } catch (Exception e) {
            throw new RuntimeException("拼音转换失败", e);
        }
    }
}

