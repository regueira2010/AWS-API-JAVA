package com.aws.dashboard.api.application.usecase;

import com.aws.dashboard.api.application.dto.CatalogEnvelopeDTO;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCatalogEnvelopeUseCaseTest {

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private GetCatalogEnvelopeUseCase useCase;

    @Test
    @DisplayName("Debe construir el Envelope calculando dinamicamente total_services y total_categories")
    void shouldBuildCatalogEnvelopeCorrectly() {
        Service mockService = Service.builder()
                .id("aws-srv-001")
                .slug(new ServiceSlug("amazon-efs"))
                .name("Amazon EFS")
                .category(new Category("cat-almacenamiento", "almacenamiento", "Almacenamiento"))
                .icon(new Icon("https://cdn.example.com/icon.svg", "svg", "32x32"))
                .details(new ServiceDetails("Desc", "https://aws.amazon.com/efs/", List.of(), true, Scope.REGIONAL, new Pricing(PricingModel.PAY_AS_YOU_GO, FreeTierType.CREDITS, "")))
                .metadata(new Metadata(true, ServiceStatus.ACTIVE, List.of(), Instant.now()))
                .build();

        when(serviceRepository.findAll()).thenReturn(List.of(mockService));
        when(serviceRepository.countUniqueCategories()).thenReturn(1L);

        CatalogEnvelopeDTO result = useCase.execute();

        assertNotNull(result);
        assertEquals("AWS Services API", result.info().name());
        assertEquals("1.1.0", result.info().contractVersion());
        assertEquals(1, result.info().totalServices());
        assertEquals(1, result.info().totalCategories());
        assertEquals(1, result.results().size());
        assertEquals("amazon-efs", result.results().get(0).slug());
    }
}