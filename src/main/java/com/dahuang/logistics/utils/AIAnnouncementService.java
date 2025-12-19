package com.dahuang.logistics.utils;

import com.dahuang.logistics.config.AiConfig;
import com.dahuang.logistics.entity.RepairAnalysisResult;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class AIAnnouncementService {

    @Autowired
    private OpenAiChatModel chatModel;

    public String generateAnnouncement(String topic, String publisher, Date date, String text) {
        String prompt = String.format(AiConfig.ANNOUNCEMENT_PROMPT_TEMPLATE, topic, publisher, date,text);
        return chatModel.chat(prompt);
    }

    public RepairAnalysisResult analyze(String content) {
        String prompt= String.format(AiConfig.TYPE_JUDGEMENT_PROMPT, content);
        String result = chatModel.chat(prompt);

        // 简单解析
        String type = extract(result, "类型");
        String urgency = extract(result, "紧急度");


        RepairAnalysisResult analysis = new RepairAnalysisResult();
        analysis.setType(type);
        analysis.setUrgency(urgency);
        return analysis;
    }

    private String extract(String text, String key) {
        String[] parts = text.split("；");
        for (String part : parts) {
            String[] keyValue = part.split("=", 2);
            if (keyValue.length == 2 && keyValue[0].trim().equals(key)) {
                return keyValue[1].trim();
            }
        }
        return "紧急度".equals(key) ? "中" : "未知";
    }
}