package com.aws.dashboard.api.application.dto;

import java.util.List;

public record CatalogEnvelopeDTO(
        InfoDTO info,
        PaginationDTO pagination,
        List<ServiceResponseDTO> results
) {
    public record InfoDTO(
            String name,
            String version,
            String contractVersion,
            String catalogVersion,
            String provider,
            long totalCategories,
            long totalServices,
            String updatedAt,
            String enrichmentProvider
    ) {}

    public record PaginationDTO(
            long total,
            int page,
            int limit,
            int pages,
            boolean hasMore,
            String nextCursor,
            String next,
            String prev
    ) {}
}