package com.dahuang.logistics.config;

import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;

@Configuration
public class OpenAiModelConfig {

    @Bean
    public OpenAiChatModel openAiChatModel() throws IOException {
        //从本地文件 中获取 apikey
        //在resources下创建文件apiKey.text,粘贴apiKey
        String rootPath = System.getProperty("user.dir");
        String filePath = rootPath + "/src/main/resources/apiKey.txt";
        String apiKey = Files.readString(Paths.get(filePath)).trim();
        // 构建对象
        return OpenAiChatModel.builder()
                .baseUrl("https://api.deepseek.com")
                .apiKey(apiKey)
                .modelName("deepseek-v4-pro")
                .timeout(Duration.ofMinutes(5))
                .temperature(0.7)
                .build();
    }
}
