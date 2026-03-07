package com.github.toku2001.service;

import java.time.LocalDate;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.toku2001.batch.model.HealthAnalysisResponse;
import com.github.toku2001.batch.model.HealthDailyMetrics;

@Service
public class OpenAiHealthAnalysisService {

    private final ObjectProvider<ChatClient.Builder> chatClientBuilderProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.health-analysis.openai-model}")
    private String model;

    @Value("${app.health-analysis.temperature:0.2}")
    private double temperature;

    public OpenAiHealthAnalysisService(ObjectProvider<ChatClient.Builder> chatClientBuilderProvider) {
        this.chatClientBuilderProvider = chatClientBuilderProvider;
    }

    public HealthAnalysisResponse analyze(LocalDate targetDate, HealthDailyMetrics metrics) {
        try {
            String metricsJson = objectMapper.writeValueAsString(metrics);
            ChatClient.Builder builder = chatClientBuilderProvider.getIfAvailable();
            if (builder == null) {
                throw new IllegalStateException("ChatClient.Builder is not available. Check OpenAI configuration.");
            }
            ChatClient chatClient = builder.build();

            String content = chatClient.prompt()
                .options(OpenAiChatOptions.builder()
                    .model(model)
                    .temperature(temperature)
                    .build())
                .system("""
                    You are a healthcare analysis assistant.
                    Analyze daily sleep and heart-rate summary data.
                    Return only a single valid JSON object with these fields:
                    score (0-100 integer),
                    condition (string),
                    findings (string array),
                    actions (string array),
                    risk_level (low|medium|high),
                    analysis_text (string, concise Japanese).
                    Do not include markdown or code fences.
                    """)
                .user("""
                    target_date: %s
                    metrics: %s
                    """.formatted(targetDate, metricsJson))
                .call()
                .content();

            String cleanedJson = extractJsonObject(content);
            JsonNode parsed = objectMapper.readTree(cleanedJson);
            String analysisText = parsed.path("analysis_text").asText("分析結果を生成しました。");
            String normalizedJson = objectMapper.writeValueAsString(parsed);

            return new HealthAnalysisResponse(analysisText, normalizedJson, content);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to analyze health metrics with OpenAI API", e);
        }
    }

    public String getModel() {
        return model;
    }

    private String extractJsonObject(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("OpenAI response content is empty");
        }

        int start = content.indexOf('{');
        int end = content.lastIndexOf('}');

        if (start < 0 || end < start) {
            throw new IllegalArgumentException("OpenAI response does not contain JSON object");
        }

        return content.substring(start, end + 1);
    }
}
