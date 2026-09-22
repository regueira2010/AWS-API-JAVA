package com.aws.dashboard.api.domain.model;

import java.util.regex.Pattern;

public record ServiceSlug(String value) {
    
    private static final Pattern SLUG_PATTERN = Pattern.compile("^[a-z0-9]+(?:-[a-z0-9]+)*$");

    public ServiceSlug {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El slug del servicio no puede ser nulo o estar vacío");
        }
        if (!SLUG_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("El slug debe estar en minúsculas y contener solo letras, números y guiones: " + value);
        }
    }
}