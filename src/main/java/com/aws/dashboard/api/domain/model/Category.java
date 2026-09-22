package com.aws.dashboard.api.domain.model;

public record Category(String id, String slug, String name) {
    public Category {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("El id de la categoria no puede estar vacio");
        if (slug == null || slug.isBlank()) throw new IllegalArgumentException("El slug de la categoria no puede estar vacio");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("El nombre de la categoria no puede estar vacio");
    }
}
