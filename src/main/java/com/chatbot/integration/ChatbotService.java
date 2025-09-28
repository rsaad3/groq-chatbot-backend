package com.chatbot.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;


@Service
public class ChatbotService {

    private final RestTemplate restTemplate;
    @Value("${app.groq-api-url}")
    private String groqApiUrl;
    @Value("${app.groq-api-key}")
    private String groqApiKey;

    public ChatbotService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }


    public String ask(String userMessage) {
        System.out.println("groqApiKey: "+groqApiKey);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(groqApiKey);

        Map<String, Object> requestBody = Map.of(
                "model", "allam-2-7b", // example Groq model
                "messages", List.of(
                        Map.of("role", "user", "content", userMessage)
                ),
                "temperature", 0.7
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                groqApiUrl,
                HttpMethod.POST,
                request,
                Map.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            var choices = (List<Map<String, Object>>) response.getBody().get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return message != null ? (String) message.get("content") : "No response";
            }
        }
        return "No response from Groq";
    }

}
