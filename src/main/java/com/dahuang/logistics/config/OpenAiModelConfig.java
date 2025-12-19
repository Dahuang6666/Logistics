package com.dahuang.logistics.config;

import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OpenAiModelConfig {

    @Bean
    public OpenAiChatModel openAiChatModel() {
        return OpenAiChatModel.builder()
                .baseUrl("https://api.deepseek.com")
                .apiKey("sk-6e6451de107a48acbb1048172fb89f69")
                .modelName("deepseek-chat")
                .timeout(Duration.ofMinutes(5))
                .temperature(0.7)
                .build();
    }
}
