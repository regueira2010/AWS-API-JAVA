package com.aws.dashboard.api.domain.model;

public record Icon(String url, String format, String dimensions) {
    public Icon {
        if (url == null || !url.startsWith("https://")) {
            throw new IllegalArgumentException("La URL del icono debe usar HTTPS estricto");
        }
        if (format == null || format.isBlank()) {
            format = "svg";
        }
    }
}