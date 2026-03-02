package com.webtopdf.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ContentCleanerService {

    private static final Logger log = LoggerFactory.getLogger(ContentCleanerService.class);

    private static final List<String> NOISE_PHRASES = List.of(
            "cookie", "accept all", "privacy policy", "terms of service",
            "subscribe", "sign up", "log in", "advertisement",
            "skip to content", "menu", "navigation", "back to top",
            "share this", "follow us");

    private static final Pattern WHITESPACE_COLLAPSE = Pattern.compile("\\s{2,}");
    private static final int MIN_LINE_WORDS = 5;
    private static final int MAX_CHARS = 12_000;

    public String clean(String rawText) {
        log.debug("Cleaning content, raw length: {}", rawText.length());
        String cleaned = Arrays.stream(rawText.split("\\.\\s+|\n"))
                .map(String::trim)
                .filter(line -> !line.isBlank())
                .filter(line -> line.split("\\s+").length >= MIN_LINE_WORDS)
                .filter(line -> {
                    String lower = line.toLowerCase();
                    return NOISE_PHRASES.stream().noneMatch(lower::contains);
                })
                .collect(Collectors.joining(" "));

        cleaned = WHITESPACE_COLLAPSE.matcher(cleaned).replaceAll(" ").trim();
        if (cleaned.length() > MAX_CHARS) {
            cleaned = cleaned.substring(0, MAX_CHARS) + "...";
        }
        log.debug("Cleaned content length: {}", cleaned.length());
        return cleaned;
    }
}
