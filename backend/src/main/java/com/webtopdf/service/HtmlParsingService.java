package com.webtopdf.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class HtmlParsingService {

    private static final Logger log = LoggerFactory.getLogger(HtmlParsingService.class);

    public String parse(String html) {
        Document doc = Jsoup.parse(html);
        Element contentRoot = selectContentRoot(doc);
        removeNoise(contentRoot);
        String parsed = extractStructuredText(contentRoot);
        log.debug("Parsed HTML content length: {}", parsed.length());
        return parsed;
    }

    private Element selectContentRoot(Document doc) {
        Element article = doc.selectFirst("article");
        if (article != null) return article;

        Element main = doc.selectFirst("main");
        if (main != null) return main;

        Element byRole = doc.selectFirst("[role=main]");
        if (byRole != null) return byRole;

        return doc.body();
    }

    private void removeNoise(Element root) {
        root.select("script, style, noscript, nav, header, footer, form, aside, iframe").remove();
        root.select(
                "[class*=cookie], [id*=cookie], [class*=modal], [id*=modal], " +
                "[class*=popup], [id*=popup], [class*=banner], [id*=banner], " +
                "[class*=subscribe], [id*=subscribe], [class*=advert], [id*=advert], " +
                "[class*=social], [id*=social], [class*=share], [id*=share], " +
                "[class*=comments], [id*=comments], [class*=related], [id*=related]"
        ).remove();
    }

    private String extractStructuredText(Element root) {
        Elements nodes = root.select("h1, h2, h3, h4, p, li, blockquote, pre");
        Set<String> lines = new LinkedHashSet<>();
        for (Element node : nodes) {
            String text = node.text().trim();
            if (text.isBlank()) continue;

            if (node.tagName().matches("h1|h2|h3|h4")) {
                lines.add(text + ":");
            } else if ("li".equals(node.tagName())) {
                lines.add("- " + text);
            } else {
                lines.add(text);
            }
        }

        if (lines.isEmpty()) {
            return root.text();
        }
        return String.join("\n", lines);
    }
}
