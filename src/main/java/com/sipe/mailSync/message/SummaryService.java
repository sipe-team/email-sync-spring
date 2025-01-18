package com.sipe.mailSync.message;

import com.fasterxml.jackson.databind.JsonNode;
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
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        var requestBody = Map.of(
            "model", "gpt-4",
            "temperature", 0.7,
            "messages", List.of(Map.of(
                "role", "user",
                "content", "Please summarize this body content in 100 characters and tell me who it's from : " + message
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
