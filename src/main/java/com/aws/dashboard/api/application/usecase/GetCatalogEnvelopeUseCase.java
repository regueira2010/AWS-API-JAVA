package com.aws.dashboard.api.application.usecase;

import com.aws.dashboard.api.application.dto.CatalogEnvelopeDTO;
import com.aws.dashboard.api.application.dto.ServiceDTOMapper;
import com.aws.dashboard.api.application.dto.ServiceResponseDTO;
import com.aws.dashboard.api.domain.model.Service;
import com.aws.dashboard.api.domain.repository.ServiceRepository;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GetCatalogEnvelopeUseCase {

    private final ServiceRepository serviceRepository;
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    public GetCatalogEnvelopeUseCase(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public CatalogEnvelopeDTO execute() {
        List<Service> services = serviceRepository.findAll();
        long totalServices = services.size();
        long totalCategories = serviceRepository.countUniqueCategories();

        List<ServiceResponseDTO> results = services.stream()
                .map(ServiceDTOMapper::toDTO)
                .toList();

        var info = new CatalogEnvelopeDTO.InfoDTO(
                "AWS Services API",
                "1.0.0",
                "1.1.0",
                "2026.09",
                "Amazon Web Services",
                totalCategories,
                totalServices,
                ISO_FORMATTER.format(Instant.now()),
                "spring-boot-backend"
        );

        var pagination = new CatalogEnvelopeDTO.PaginationDTO(
                totalServices,
                1,
                (int) Math.max(totalServices, 1),
                1,
                false,
                null,
                null,
                null
        );

        return new CatalogEnvelopeDTO(info, pagination, results);
    }
}
