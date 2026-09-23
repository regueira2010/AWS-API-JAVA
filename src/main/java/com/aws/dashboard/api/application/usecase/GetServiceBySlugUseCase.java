package com.aws.dashboard.api.application.usecase;

import com.aws.dashboard.api.application.dto.ServiceDTOMapper;
import com.aws.dashboard.api.application.dto.ServiceResponseDTO;
import com.aws.dashboard.api.domain.model.ServiceSlug;
import com.aws.dashboard.api.domain.repository.ServiceRepository;

import org.springframework.cache.annotation.Cacheable;

public class GetServiceBySlugUseCase {

    private final ServiceRepository serviceRepository;

    public GetServiceBySlugUseCase(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Cacheable(value = "serviceDetailCache", key = "#rawSlug")
    public ServiceResponseDTO execute(String rawSlug) {
        ServiceSlug slug = new ServiceSlug(rawSlug);
        return serviceRepository.findBySlug(slug)
                .map(ServiceDTOMapper::toDTO)
                .orElse(null);
    }
}