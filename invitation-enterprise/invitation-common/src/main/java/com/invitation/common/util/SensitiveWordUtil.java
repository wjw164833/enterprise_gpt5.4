package com.invitation.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 敏感词过滤工具 - DFA算法实现
 */
@Slf4j
@Component
public class SensitiveWordUtil {

    /** DFA敏感词字典 */
    private Map<Character, Object> sensitiveWordMap = new HashMap<>();

    /** 默认替换字符 */
    private static final String REPLACEMENT = "***";

    /**
     * 初始化敏感词库
     */
    public void initSensitiveWords(Set<String> words) {
        Map<Character, Object> newMap = new HashMap<>(words.size());
        for (String word : words) {
            Map<Character, Object> currentMap = newMap;
            for (int i = 0; i < word.length(); i++) {
                char c = word.charAt(i);
                @SuppressWarnings("unchecked")
                Map<Character, Object> nextMap = (Map<Character, Object>) currentMap.get(c);
                if (nextMap == null) {
                    nextMap = new HashMap<>();
                    currentMap.put(c, nextMap);
                }
                currentMap = nextMap;
                if (i == word.length() - 1) {
                    currentMap.put('\0', null); // 结束标记
                }
            }
        }
        this.sensitiveWordMap = newMap;
        log.info("敏感词库初始化完成，共{}个词", words.size());
    }

    /**
     * 检查文本是否包含敏感词
     */
    public boolean contains(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            int matchLength = matchWord(text, i);
            if (matchLength > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 过滤敏感词，替换为***
     */
    public String filter(String text) {
        return filter(text, REPLACEMENT);
    }

    /**
     * 过滤敏感词，替换为指定字符串
     */
    public String filter(String text, String replacement) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        StringBuilder result = new StringBuilder(text);
        for (int i = 0; i < result.length(); i++) {
            int matchLength = matchWord(result.toString(), i);
            if (matchLength > 0) {
                for (int j = i; j < i + matchLength; j++) {
                    result.setCharAt(j, replacement.charAt(j - i < replacement.length() ? j - i : replacement.length() - 1));
                }
                i += matchLength - 1;
            }
        }
        return result.toString();
    }

    /**
     * 获取文本中的敏感词列表
     */
    public Set<String> getSensitiveWords(String text) {
        Set<String> words = new HashSet<>();
        if (text == null || text.isEmpty()) {
            return words;
        }
        for (int i = 0; i < text.length(); i++) {
            int matchLength = matchWord(text, i);
            if (matchLength > 0) {
                words.add(text.substring(i, i + matchLength));
            }
        }
        return words;
    }

    /**
     * DFA匹配敏感词
     */
    @SuppressWarnings("unchecked")
    private int matchWord(String text, int startIndex) {
        Map<Character, Object> currentMap = sensitiveWordMap;
        int matchLength = 0;
        for (int i = startIndex; i < text.length(); i++) {
            char c = text.charAt(i);
            Object next = currentMap.get(c);
            if (next == null) {
                break;
            }
            matchLength++;
            currentMap = (Map<Character, Object>) next;
            if (currentMap.containsKey('\0')) {
                return matchLength;
            }
        }
        return 0;
    }
}
