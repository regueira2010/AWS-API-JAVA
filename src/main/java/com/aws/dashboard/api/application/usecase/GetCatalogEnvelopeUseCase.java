package com.aws.dashboard.api.application.usecase;

import com.aws.dashboard.api.application.dto.CatalogEnvelopeDTO;
import com.aws.dashboard.api.application.dto.ServiceDTOMapper;
import com.aws.dashboard.api.application.dto.ServiceResponseDTO;
import com.aws.dashboard.api.domain.model.Service;
import com.aws.dashboard.api.domain.repository.ServiceRepository;

import org.springframework.cache.annotation.Cacheable;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GetCatalogEnvelopeUseCase {

    private final ServiceRepository serviceRepository;
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    public GetCatalogEnvelopeUseCase(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public CatalogEnvelopeDTO execute() {
        return execute(null, null);
    }

    @Cacheable(value = "catalogCache", key = "(#page == null ? '1' : #page) + '-' + (#limit == null ? 'all' : #limit)")
    public CatalogEnvelopeDTO execute(Integer page, Integer limit) {
        List<Service> services = serviceRepository.findAll();
        long totalServices = services.size();
        long totalCategories = serviceRepository.countUniqueCategories();

        int effectivePage = (page != null && page > 0) ? page : 1;
        int effectiveLimit = (limit != null && limit > 0) ? limit : (int) Math.max(totalServices, 1);
        int totalPages = (int) Math.ceil((double) totalServices / effectiveLimit);
        if (totalPages == 0) totalPages = 1;

        int fromIndex = Math.min((effectivePage - 1) * effectiveLimit, (int) totalServices);
        int toIndex = Math.min(fromIndex + effectiveLimit, (int) totalServices);

        List<ServiceResponseDTO> pagedResults = services.subList(fromIndex, toIndex).stream()
                .map(ServiceDTOMapper::toDTO)
                .toList();

        boolean hasMore = effectivePage < totalPages;
        String nextUrl = hasMore ? "/api/v1/services?page=" + (effectivePage + 1) + "&limit=" + effectiveLimit : null;
        String prevUrl = effectivePage > 1 ? "/api/v1/services?page=" + (effectivePage - 1) + "&limit=" + effectiveLimit : null;

        var info = new CatalogEnvelopeDTO.InfoDTO(
                "AWS Services API",
                "1.0.0",
                "1.1.0",
                "2026.09",
                "Amazon Web Services",
                totalCategories,
                totalServices,
                ISO_FORMATTER.format(Instant.now()),
                "aws-catalog-pipeline"
        );

        var pagination = new CatalogEnvelopeDTO.PaginationDTO(
                totalServices,
                effectivePage,
                effectiveLimit,
                totalPages,
                hasMore,
                null,
                nextUrl,
                prevUrl
        );

        return new CatalogEnvelopeDTO(info, pagination, pagedResults);
    }
}
