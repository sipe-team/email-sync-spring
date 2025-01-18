package com.sipe.mailSync.message;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SummaryService {
    private final RestTemplate restTemplate;
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    @Value("${openapi.key}")
    private String apiKey;

    public SummaryService(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getSummary(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode messageNode;
        try {
            messageNode = objectMapper.readTree(message);
        } catch (IOException e) {
            throw new RuntimeException("JSON Parsing error : " + e.getMessage(), e);
        }

        if (messageNode == null || !messageNode.has("payload") || !messageNode.get("payload").has("parts")) {
            throw new IllegalArgumentException("Invalid JSON structure: missing payload or parts");
        }

        ArrayNode parts = (ArrayNode) messageNode.get("payload").get("parts");
        StringBuilder concatenatedData = new StringBuilder();
        for (JsonNode part : parts) {
            if (part.has("body") && part.get("body").has("data")) {
                concatenatedData.append(part.get("body").get("data").asText()).append(" ");
            }
        }
        String bodyMessage = concatenatedData.toString().trim();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        var requestBody = Map.of(
            "model", "gpt-4o",
            "messages", List.of(Map.of(
                "role", "user",
                "content", "이 메일의 내용만 최대 100자로 한글로 요약해줘 : " + bodyMessage
            ))
        );
        var entity = new HttpEntity<>(requestBody, headers);

        try {
            JsonNode response = restTemplate.postForObject(API_URL, entity, JsonNode.class);
            if (response == null || !response.has("choices")) {
                throw new IllegalStateException("Invalid response");
            }
            return response.get("choices").get(0).get("message").get("content").asText();
        } catch (Exception e) {
            throw new RuntimeException("error:(", e);
        }
    }
}
