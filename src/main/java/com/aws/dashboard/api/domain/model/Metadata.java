package com.aws.dashboard.api.domain.model;

import java.time.Instant;
import java.util.List;

public record Metadata(
        boolean isActive,
        ServiceStatus status,
        List<String> tags,
        Instant updatedAt
) {
    public Metadata {
        if (status == null) status = ServiceStatus.UNKNOWN;
        if (tags == null) tags = List.of();
        if (updatedAt == null) updatedAt = Instant.now();
    }
}