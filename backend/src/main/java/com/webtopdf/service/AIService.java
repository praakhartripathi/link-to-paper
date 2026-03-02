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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class AIService {

    private static final Logger log = LoggerFactory.getLogger(AIService.class);

    @Value("${gemini.api-key:}")
    private String geminiApiKey;

    @Value("${gemini.model:gemini-1.5-flash}")
    private String geminiModel;

    @Value("${gemini.api-url:https://generativelanguage.googleapis.com/v1beta/models}")
    private String geminiApiUrl;

    private static final MediaType JSON_MEDIA = MediaType.get("application/json; charset=utf-8");

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build();

    public PaperResponse generatePaper(String pageTitle, String cleanedContent) throws Exception {
        if (!isRealKey(geminiApiKey)) {
            log.warn("No real Gemini API key detected — returning mock paper.");
            return mockPaper(pageTitle);
        }

        String prompt = buildPrompt(pageTitle, cleanedContent);

        log.info("Calling Gemini (model={}) for: {}", geminiModel, pageTitle);
        try {
            String responseJson = callGemini(prompt, geminiApiKey, geminiModel);
            return parsePaperResponse(responseJson, pageTitle);
        } catch (Exception geminiEx) {
            log.error("Gemini call failed: {}", geminiEx.getMessage());
        }

        log.error("Gemini generation failed, returning mock paper.");
        return mockPaper(pageTitle);
    }

    private boolean isRealKey(String key) {
        return key != null && !key.isBlank() && !key.startsWith("YOUR_");
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

    private String callGemini(String userMessage, String apiKey, String model) throws Exception {
        ObjectNode root = objectMapper.createObjectNode();
        ArrayNode contents = root.putArray("contents");
        ObjectNode contentObj = contents.addObject();
        ArrayNode parts = contentObj.putArray("parts");
        parts.addObject().put("text", userMessage);
        root.putObject("generationConfig").put("temperature", 0.7);

        String baseUrl = geminiApiUrl.endsWith("/") ? geminiApiUrl.substring(0, geminiApiUrl.length() - 1) : geminiApiUrl;
        String requestUrl = baseUrl + "/" + model + ":generateContent?key=" +
                URLEncoder.encode(apiKey, StandardCharsets.UTF_8);

        Request request = new Request.Builder()
                .url(requestUrl)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(objectMapper.writeValueAsString(root), JSON_MEDIA))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "no body";
                throw new RuntimeException("Gemini API error " + response.code() + ": " + errorBody);
            }
            return response.body().string();
        }
    }

    private PaperResponse parsePaperResponse(String providerJson, String fallbackTitle) {
        try {
            JsonNode root = objectMapper.readTree(providerJson);
            String content = extractAssistantContent(root);
            if (content == null || content.isBlank()) {
                throw new RuntimeException("Provider response content was empty");
            }
            content = content.strip();
            if (content.startsWith("```")) {
                content = content.replaceAll("(?s)```json\\s*", "").replaceAll("```", "").strip();
            }
            int firstBrace = content.indexOf('{');
            int lastBrace = content.lastIndexOf('}');
            if (!(content.startsWith("{") && content.endsWith("}")) &&
                    firstBrace >= 0 && lastBrace > firstBrace) {
                content = content.substring(firstBrace, lastBrace + 1).trim();
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
            log.error("Failed to parse provider response, falling back to mock", e);
            return mockPaper(fallbackTitle);
        }
    }

    private String extractAssistantContent(JsonNode root) {
        JsonNode choices = root.path("choices");
        if (choices.isArray() && !choices.isEmpty()) {
            return choices.get(0).path("message").path("content").asText("");
        }

        JsonNode candidates = root.path("candidates");
        if (candidates.isArray() && !candidates.isEmpty()) {
            JsonNode parts = candidates.get(0).path("content").path("parts");
            if (parts.isArray()) {
                StringBuilder text = new StringBuilder();
                for (JsonNode part : parts) {
                    String partText = part.path("text").asText("");
                    if (!partText.isBlank()) {
                        if (!text.isEmpty()) text.append('\n');
                        text.append(partText);
                    }
                }
                return text.toString();
            }
        }
        return "";
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
