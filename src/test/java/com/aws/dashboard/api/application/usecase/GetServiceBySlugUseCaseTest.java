package com.aws.dashboard.api.application.usecase;

import com.aws.dashboard.api.application.dto.ServiceResponseDTO;
import com.aws.dashboard.api.domain.model.*;

import com.aws.dashboard.api.domain.repository.ServiceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetServiceBySlugUseCaseTest {

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private GetServiceBySlugUseCase useCase;

    @Test
    @DisplayName("Debe retornar el ServiceResponseDTO si el slug existe en el repositorio")
    void shouldReturnServiceDTOWhenSlugExists() {
        Service mockService = Service.builder()
                .id("aws-srv-001")
                .slug(new ServiceSlug("amazon-efs"))
                .name("Amazon EFS")
                .type("service")
                .category(new Category("cat-almacenamiento", "almacenamiento", "Almacenamiento"))
                .icon(new Icon("https://cdn.example.com/icon.svg", "svg", "32x32"))
                .details(new ServiceDetails("Desc", "https://aws.amazon.com/efs/", List.of(), true, Scope.REGIONAL, new Pricing(PricingModel.PAY_AS_YOU_GO, FreeTierType.CREDITS, "")))
                .metadata(new Metadata(true, ServiceStatus.ACTIVE, List.of(), Instant.now()))
                .build();

        when(serviceRepository.findBySlug(any())).thenReturn(Optional.of(mockService));

        ServiceResponseDTO result = useCase.execute("amazon-efs");

        assertNotNull(result);
        assertEquals("amazon-efs", result.slug());
        assertEquals("Amazon EFS", result.name());
    }

    @Test
    @DisplayName("Debe retornar null si el slug no existe")
    void shouldReturnNullWhenSlugNotFound() {
        when(serviceRepository.findBySlug(any())).thenReturn(Optional.empty());

        ServiceResponseDTO result = useCase.execute("non-existent");

        assertNull(result);
    }
}