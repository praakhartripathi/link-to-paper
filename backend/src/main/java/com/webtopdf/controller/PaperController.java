package com.webtopdf.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webtopdf.dto.PaperRequest;
import com.webtopdf.dto.PaperResponse;
import com.webtopdf.model.Paper;
import com.webtopdf.repository.PaperRepository;
import com.webtopdf.service.AIService;
import com.webtopdf.service.ContentCleanerService;
import com.webtopdf.service.PdfGeneratorService;
import com.webtopdf.service.WebScraperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PaperController {

    private static final Logger log = LoggerFactory.getLogger(PaperController.class);

    private final WebScraperService     scraperService;
    private final ContentCleanerService cleanerService;
    private final AIService             aiService;
    private final PdfGeneratorService   pdfGeneratorService;
    private final PaperRepository       paperRepository;
    private final ObjectMapper          objectMapper;

    public PaperController(WebScraperService scraperService,
                           ContentCleanerService cleanerService,
                           AIService aiService,
                           PdfGeneratorService pdfGeneratorService,
                           PaperRepository paperRepository,
                           ObjectMapper objectMapper) {
        this.scraperService     = scraperService;
        this.cleanerService     = cleanerService;
        this.aiService          = aiService;
        this.pdfGeneratorService = pdfGeneratorService;
        this.paperRepository    = paperRepository;
        this.objectMapper       = objectMapper;
    }

    @PostMapping("/generate-paper")
    public ResponseEntity<byte[]> generatePaper(@RequestBody PaperRequest request) {
        log.info("Received request to generate paper for URL: {}", request.url());
        try {
            String[] scraped      = scraperService.scrape(request.url());
            String pageTitle      = scraped[0];
            String rawBody        = scraped[1];
            String cleanedContent = cleanerService.clean(rawBody);
            PaperResponse paper   = aiService.generatePaper(pageTitle, cleanedContent);
            byte[] pdfBytes       = pdfGeneratorService.generate(paper);

            // Persist to DB (graceful degradation)
            try {
                String refsJson = objectMapper.writeValueAsString(
                        paper.references() == null ? new java.util.ArrayList<>() : paper.references());
                Paper saved = paperRepository.save(Paper.builder()
                        .url(request.url())
                        .title(paper.title())
                        .abstractText(paper.abstractText())
                        .introduction(paper.introduction())
                        .methodology(paper.methodology())
                        .discussion(paper.discussion())
                        .conclusion(paper.conclusion())
                        .referencesJson(refsJson)
                        .pdfSizeBytes(pdfBytes.length)
                        .build());
                log.info("Paper persisted with id={}", saved.getId());
            } catch (Exception dbEx) {
                log.warn("Could not persist paper to DB (continuing): {}", dbEx.getMessage());
            }

            String filename = sanitizeFilename(paper.title()) + ".pdf";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(pdfBytes.length);

            log.info("Paper served: '{}', {} bytes", filename, pdfBytes.length);
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error generating paper for URL {}: {}", request.url(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("{\"status\":\"ok\",\"ai\":\"mistral\",\"db\":\"mysql\"}");
    }

    private String sanitizeFilename(String title) {
        if (title == null) return "paper";
        String safe = title.replaceAll("[^a-zA-Z0-9 _-]", "").replace(' ', '_');
        return safe.substring(0, Math.min(safe.length(), 60));
    }
}
