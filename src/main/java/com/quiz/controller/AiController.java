package com.quiz.controller;

import com.quiz.service.AiService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
@PreAuthorize("hasRole('ADMIN')")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/generate-questions")
    public ResponseEntity<List<Map<String, Object>>> generateQuestions(@RequestBody GenerateRequest request) {
        if (request.getTopic() == null || request.getTopic().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        int count = request.getCount() != null && request.getCount() > 0 ? request.getCount() : 10;
        if (count > 50) count = 50; // Cap to avoid massive API costs/timeouts
        
        List<Map<String, Object>> questions = aiService.generateQuestions(request.getTopic(), count);
        return ResponseEntity.ok(questions);
    }

    public static class GenerateRequest {
        private String topic;
        private Integer count;

        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }
        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }
    }
}
