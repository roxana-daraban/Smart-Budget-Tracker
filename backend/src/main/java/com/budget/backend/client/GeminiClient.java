package com.budget.backend.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Apel REST către Google Gemini generateContent (fără Spring AI).
 * Folosește Jackson 3 ({@link JsonMapper}) — stack-ul implicit din Spring Boot 4.
 */
@Component
public class GeminiClient {

    /** Model stabil curent pentru generateContent; 1.5-* poate returna 404 pe API-ul developer. */
    private static final String DEFAULT_MODEL = "gemini-2.5-flash";

    private final RestTemplate restTemplate;
    private final JsonMapper jsonMapper;
    private final String apiKey;
    private final String model;

    public GeminiClient(
            JsonMapper jsonMapper,
            @Value("${gemini.api.key:}") String apiKey,
            @Value("${gemini.api.model:" + DEFAULT_MODEL + "}") String model) {
        this.jsonMapper = jsonMapper;
        this.apiKey = apiKey != null ? apiKey.trim() : "";
        this.model = (model != null && !model.isBlank()) ? model.trim() : DEFAULT_MODEL;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(10));
        factory.setReadTimeout(Duration.ofSeconds(60));
        this.restTemplate = new RestTemplate(factory);
    }

    public String generateContent(String userPrompt) {
        if (apiKey.isEmpty() || "YOUR_GEMINI_API_KEY_HERE".equals(apiKey)) {
            throw new RuntimeException("Gemini API key is not configured. Set gemini.api.key in application.properties.");
        }

        String keyQ = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);
        String url = String.format(
                "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                model,
                keyQ
        );

        Map<String, Object> part = new HashMap<>();
        part.put("text", userPrompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(part));

        Map<String, Object> body = new HashMap<>();
        body.put("contents", List.of(content));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            String raw = restTemplate.postForObject(url, entity, String.class);
            if (raw == null || raw.isBlank()) {
                throw new RuntimeException("Empty response from Gemini API");
            }
            JsonNode root = jsonMapper.readTree(raw);
            if (root.has("error")) {
                String msg = root.path("error").path("message").asString("Gemini API error");
                throw new RuntimeException(msg);
            }
            JsonNode textNode = root.path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text");
            if (textNode.isMissingNode() || textNode.asString().isBlank()) {
                throw new RuntimeException("No text in Gemini response");
            }
            return textNode.asString().trim();
        } catch (RestClientResponseException e) {
            throw new RuntimeException("Gemini API HTTP error: " + e.getStatusCode() + " — " + e.getResponseBodyAsString(), e);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to call Gemini API: " + e.getMessage(), e);
        }
    }
}
