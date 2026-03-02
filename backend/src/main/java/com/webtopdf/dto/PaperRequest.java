package com.webtopdf.dto;

import jakarta.validation.constraints.NotBlank;

public record PaperRequest(
        @NotBlank(message = "URL must not be blank")
        String url
) {}
