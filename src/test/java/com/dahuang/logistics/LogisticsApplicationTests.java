package com.dahuang.logistics;

import com.dahuang.logistics.config.AiConfig;
import com.dahuang.logistics.entity.RepairAnalysisResult;
import com.dahuang.logistics.utils.PasswordUtils;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
class LogisticsApplicationTests {


    @Autowired
    private OpenAiChatModel chatModel;

    @Test
    void contextLoads() {
    }


        @Test
        void test() {
            String password = "123456";
            // 加密密码
            String encodedPassword = PasswordUtils.encode(password);
            System.out.println("加密后的密码: " + encodedPassword);

            // 验证密码
            String encodePassword ="$2a$10$iWscnO1ehbu13AxdAoR/ferB0u73a1SWk8ROcUDHb4CrWaCSM7jP.";
            boolean isMatch = PasswordUtils.matches(password, encodedPassword);
            System.out.println("密码验证结果: " + isMatch);
        }


    @Test
    public void analyze() {
        String content = "床从窗户飞了 😃";
        String prompt = String.format(AiConfig.TYPE_JUDGEMENT_PROMPT, content);
        String result = chatModel.chat(prompt);
        System.out.println("AI返回结果：\n" + result);

        // 解析（使用与extract方法匹配的参数）
        String type = extract(result, "类型");
        String urgency = extract(result, "紧急度");

        RepairAnalysisResult analysis = new RepairAnalysisResult();
        analysis.setType(type);
        analysis.setUrgency(urgency);
        System.out.println("解析后的类型：" + analysis.getType());
        System.out.println("解析后的紧急度：" + analysis.getUrgency());
    }

    private String extract(String text, String key) {
        // 按“；”分割成两部分
        String[] parts = text.split("；");
        for (String part : parts) {
            // 按“=”分割key和value（适配提示词中的格式）
            String[] keyValue = part.split("=", 2); // 最多分割成2部分
            if (keyValue.length == 2 && keyValue[0].trim().equals(key)) {
                return keyValue[1].trim(); // 返回value
            }
        }
        // 解析失败时返回默认值（避免空值）
        return "紧急度".equals(key) ? "中" : "未知"; // 紧急度默认"中"，类型默认"未知"
    }


    @Test
    public void TestDir (){
        System.out.println( System.getProperty("user.dir"));
    }
}
