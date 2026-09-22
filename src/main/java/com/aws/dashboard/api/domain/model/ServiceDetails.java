package com.aws.dashboard.api.domain.model;

import java.util.List;

public record ServiceDetails(
        String description,
        String awsDocUrl,
        List<String> useCases,
        boolean freeTier,
        Scope scope,
        Pricing pricing
) {
    public ServiceDetails {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("La descripcion del servicio no puede estar vacia");
        }
        if (awsDocUrl == null || !awsDocUrl.startsWith("https://")) {
            throw new IllegalArgumentException("La URL de la documentacion oficial debe usar HTTPS");
        }
        if (useCases == null) useCases = List.of();
        if (scope == null) scope = Scope.UNKNOWN;
        if (pricing == null) pricing = new Pricing(PricingModel.UNKNOWN, FreeTierType.UNKNOWN, "");
    }
}