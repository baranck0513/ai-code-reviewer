package com.musti.codereviewer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ClaudeService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${claude.api.model:claude-sonnet-4-20250514}")
    private String model;

    @Value("${claude.api.max-tokens:4096}")
    private int maxTokens;

    public ClaudeService(
            @Value("${claude.api.key}") String apiKey,
            @Value("${claude.api.url:https://api.anthropic.com/v1/messages}") String apiUrl) {

        this.objectMapper = new ObjectMapper();
        this.restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("x-api-key", apiKey)
                .defaultHeader("anthropic-version", "2023-06-01")
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public String reviewCode(String code, String language, String description) {
        log.info("Sending code review request to Claude API. Language: {}", language);

        String systemPrompt = buildSystemPrompt();
        String userPrompt = buildUserPrompt(code, language, description);

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "max_tokens", maxTokens,
                "system", systemPrompt,
                "messages", List.of(
                        Map.of("role", "user", "content", userPrompt)
                )
        );

        try {
            String response = restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            return extractContentFromResponse(response);

        } catch (Exception e) {
            log.error("Error calling Claude API: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get code review from Claude API: " + e.getMessage(), e);
        }
    }

    private String buildSystemPrompt() {
        return """
                You are an expert code reviewer. Your task is to analyze the provided code and give 
                constructive feedback. Focus on:
                
                1. **Code Quality**: Readability, naming conventions, structure
                2. **Potential Bugs**: Logic errors, edge cases, null handling
                3. **Performance**: Inefficiencies, optimization opportunities
                4. **Security**: Vulnerabilities, input validation, data exposure
                5. **Best Practices**: Design patterns, SOLID principles, language-specific conventions
                
                Provide your review in a clear, organized format with specific line references when applicable.
                Be constructive and educational in your feedback. If the code is good, acknowledge what's done well.
                
                Keep your response concise but thorough. Use markdown formatting for better readability.
                """;
    }

    private String buildUserPrompt(String code, String language, String description) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Please review the following code:\n\n");

        if (language != null && !language.isBlank()) {
            prompt.append("**Language:** ").append(language).append("\n\n");
        }

        if (description != null && !description.isBlank()) {
            prompt.append("**Context:** ").append(description).append("\n\n");
        }

        prompt.append("```");
        if (language != null && !language.isBlank()) {
            prompt.append(language);
        }
        prompt.append("\n").append(code).append("\n```");

        return prompt.toString();
    }

    private String extractContentFromResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode content = root.path("content");

            if (content.isArray() && !content.isEmpty()) {
                JsonNode firstContent = content.get(0);
                if (firstContent.has("text")) {
                    return firstContent.get("text").asText();
                }
            }

            log.warn("Unexpected response format from Claude API: {}", response);
            return "Unable to parse review response";

        } catch (Exception e) {
            log.error("Error parsing Claude API response: {}", e.getMessage());
            throw new RuntimeException("Failed to parse Claude API response", e);
        }
    }
}