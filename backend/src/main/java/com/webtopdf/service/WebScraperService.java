package com.webtopdf.service;

import com.webtopdf.dto.ScrapedPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WebScraperService {

    private static final Logger log = LoggerFactory.getLogger(WebScraperService.class);

    @Value("${scraper.timeout-ms:10000}")
    private int timeoutMs;

    public ScrapedPage scrape(String url) throws Exception {
        log.info("Scraping URL: {}", url);
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (compatible; WebToPdf/1.0)")
                .timeout(timeoutMs)
                .get();
        String title = doc.title();
        String html = doc.html();
        log.debug("Scraped title: '{}', html chars: {}", title, html.length());
        return new ScrapedPage(title, html);
    }
}
