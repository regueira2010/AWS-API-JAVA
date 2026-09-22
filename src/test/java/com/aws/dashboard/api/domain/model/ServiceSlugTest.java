package com.aws.dashboard.api.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ServiceSlugTest {

    @Test
    @DisplayName("Debe crear un ServiceSlug válido cuando cumple las reglas del contrato")
    void shouldCreateValidSlug() {
        String validSlugValue = "amazon-efs";
        ServiceSlug slug = new ServiceSlug(validSlugValue);
        
        assertEquals(validSlugValue, slug.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Amazon-EFS", "amazon_efs", "amazon efs", "amazon.efs", "efs!", ""})
    @DisplayName("Debe lanzar IllegalArgumentException ante slugs inválidos")
    void shouldThrowExceptionForInvalidSlugs(String invalidSlug) {
        assertThrows(IllegalArgumentException.class, () -> new ServiceSlug(invalidSlug));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException si el slug es nulo")
    void shouldThrowExceptionForNullSlug() {
        assertThrows(IllegalArgumentException.class, () -> new ServiceSlug(null));
    }
}