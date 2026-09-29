package com.quiz.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quiz.entity.SystemConfig;
import com.quiz.repository.SystemConfigRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@Service
public class AiService {

    private final SystemConfigRepository systemConfigRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AiService(SystemConfigRepository systemConfigRepository) {
        this.systemConfigRepository = systemConfigRepository;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    private String getApiKey() {
        return systemConfigRepository.findById("gemini_api_key")
                .map(SystemConfig::getValue)
                .orElseThrow(() -> new RuntimeException("Gemini API key not found in system table"));
    }

    public List<Map<String, Object>> generateQuestions(String topic, int count) {
        String apiKey = getApiKey();
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-lite:generateContent?key=" + apiKey;

        String prompt = "You are a quiz question generator. Generate " + count + " multiple-choice questions about '" + topic + "'. " +
                "Return ONLY a valid JSON array of objects. Each object must have the following format exactly: " +
                "{\"q\": \"question text\", \"options\": [\"option1\", \"option2\", \"option3\", \"option4\"], \"a\": \"correct option\"}. " +
                "The correct option ('a') must exactly match one of the items in the 'options' array. " +
                "Do not include markdown blocks like ```json or any other text, just the JSON array.";

        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> content = new HashMap<>();
        List<Map<String, String>> parts = new ArrayList<>();
        Map<String, String> part = new HashMap<>();
        part.put("text", prompt);
        parts.add(part);
        content.put("parts", parts);
        contents.add(content);
        requestBody.put("contents", contents);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        int maxRetries = 3;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
                JsonNode root = objectMapper.readTree(response.getBody());
                String textResponse = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
                
                // Clean up the text response in case the model ignored the instructions and wrapped it in markdown
                textResponse = textResponse.trim();
                if (textResponse.startsWith("```json")) {
                    textResponse = textResponse.substring(7);
                } else if (textResponse.startsWith("```")) {
                    textResponse = textResponse.substring(3);
                }
                if (textResponse.endsWith("```")) {
                    textResponse = textResponse.substring(0, textResponse.length() - 3);
                }
                
                return objectMapper.readValue(textResponse.trim(), objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));
            } catch (Exception e) {
                if (attempt == maxRetries) {
                    throw new RuntimeException("Failed to generate questions from Gemini API after " + maxRetries + " attempts: " + e.getMessage(), e);
                }
                try {
                    Thread.sleep(2000); // Wait 2 seconds before retrying
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread interrupted during retry delay", ie);
                }
            }
        }
        throw new RuntimeException("Failed to generate questions from Gemini API");
    }
}
