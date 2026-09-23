package com.aws.dashboard.api.infrastructure.config;

import com.aws.dashboard.api.application.usecase.GetCatalogEnvelopeUseCase;
import com.aws.dashboard.api.application.usecase.GetServiceBySlugUseCase;
import com.aws.dashboard.api.domain.repository.ServiceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public GetCatalogEnvelopeUseCase getCatalogEnvelopeUseCase(ServiceRepository serviceRepository) {
        return new GetCatalogEnvelopeUseCase(serviceRepository);
    }

    @Bean
    public GetServiceBySlugUseCase getServiceBySlugUseCase(ServiceRepository serviceRepository) {
        return new GetServiceBySlugUseCase(serviceRepository);
    }
}