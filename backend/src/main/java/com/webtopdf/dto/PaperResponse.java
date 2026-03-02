package com.webtopdf.dto;

import java.util.List;

public record PaperResponse(
        String title,
        String abstractText,
        String introduction,
        String methodology,
        String discussion,
        String conclusion,
        List<String> references
) {}
