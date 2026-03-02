package com.webtopdf.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.webtopdf.dto.PaperResponse;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class AIService {

    private static final Logger log = LoggerFactory.getLogger(AIService.class);

    @Value("${mistral.api-key}")
    private String apiKey;

    @Value("${mistral.model:mistral-small-latest}")
    private String model;

    @Value("${mistral.api-url:https://api.mistral.ai/v1/chat/completions}")
    private String apiUrl;

    private static final MediaType JSON_MEDIA = MediaType.get("application/json; charset=utf-8");

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build();

    public PaperResponse generatePaper(String pageTitle, String cleanedContent) throws Exception {
        if (apiKey == null || apiKey.isBlank() || apiKey.startsWith("YOUR_")) {
            log.warn("No real Mistral API key detected — returning mock paper.");
            return mockPaper(pageTitle);
        }
        log.info("Calling Mistral AI (model={}) for: {}", model, pageTitle);
        String responseJson = callMistral(buildPrompt(pageTitle, cleanedContent));
        return parsePaperResponse(responseJson, pageTitle);
    }

    private String buildPrompt(String title, String content) {
        return """
                You are an expert academic writer. Convert the following web article into a \
                well-structured technical/research paper. Return your response as a single, \
                raw JSON object (no markdown fences) with EXACTLY these keys \
                ("references" is a JSON array of strings, everything else is a string):
                {"title":"...","abstractText":"...","introduction":"...","methodology":"...",\
                "discussion":"...","conclusion":"...","references":["..."]}
                Article Title: %s
                Article Content: %s
                """.formatted(title, content);
    }

    private String callMistral(String userMessage) throws Exception {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("model", model);
        root.put("temperature", 0.7);
        ArrayNode messages = root.putArray("messages");
        ObjectNode msg = messages.addObject();
        msg.put("role", "user");
        msg.put("content", userMessage);

        Request request = new Request.Builder()
                .url(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(objectMapper.writeValueAsString(root), JSON_MEDIA))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "no body";
                throw new RuntimeException("Mistral API error " + response.code() + ": " + errorBody);
            }
            return response.body().string();
        }
    }

    private PaperResponse parsePaperResponse(String mistralJson, String fallbackTitle) {
        try {
            JsonNode root = objectMapper.readTree(mistralJson);
            String content = root.path("choices").get(0).path("message").path("content").asText();
            content = content.strip();
            if (content.startsWith("```")) {
                content = content.replaceAll("(?s)```json\\s*", "").replaceAll("```", "").strip();
            }
            JsonNode paper = objectMapper.readTree(content);
            List<String> refs = new ArrayList<>();
            JsonNode refsNode = paper.path("references");
            if (refsNode.isArray()) refsNode.forEach(r -> refs.add(r.asText()));
            return new PaperResponse(
                    paper.path("title").asText(fallbackTitle),
                    paper.path("abstractText").asText(""),
                    paper.path("introduction").asText(""),
                    paper.path("methodology").asText(""),
                    paper.path("discussion").asText(""),
                    paper.path("conclusion").asText(""),
                    refs);
        } catch (Exception e) {
            log.error("Failed to parse Mistral response, falling back to mock", e);
            return mockPaper(fallbackTitle);
        }
    }

    private PaperResponse mockPaper(String title) {
        return new PaperResponse(
                title.isBlank() ? "Technical Paper" : title,
                "This paper presents an analysis of the content retrieved from the provided URL.",
                "The rapid proliferation of online information has created a need for systematic tools " +
                "that can extract, structure, and present web content in academically rigorous formats.",
                "The system employs a multi-stage pipeline: URL fetching via Jsoup, HTML parsing and noise " +
                "filtering, prompt engineering for structured AI output, and iText-based PDF rendering.",
                "The generated paper successfully preserves the core arguments and findings of the source " +
                "material while reformatting them into a structure familiar to academic readers.",
                "This work demonstrates that automated conversion of web articles to technical papers " +
                "is both technically feasible and practically valuable for researchers and students.",
                List.of("Mistral AI. (2024). Mistral Small Model Documentation.",
                        "Cai, P. et al. (2023). Jsoup: Java HTML Parser.",
                        "iText Group. (2024). iText 7 Core Documentation.",
                        "Source: " + title));
    }
}
