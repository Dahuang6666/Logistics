package com.dahuang.logistics.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class RandomNumber {
    private static final String CHARACTERS = "0123456789";  // 随机数可选字符范围
    private static final int CODE_LENGTH = 6;  // 随机数长度
    public String generateRandomCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length()); // 随机选择一个字符
            code.append(CHARACTERS.charAt(index));
        }
        return code.toString();
    }
}
