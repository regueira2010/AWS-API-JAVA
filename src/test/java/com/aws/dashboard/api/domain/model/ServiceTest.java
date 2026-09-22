package com.aws.dashboard.api.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTest {

    @Test
    @DisplayName("Debe crear un Service de Dominio valido con todos sus componentes")
    void shouldCreateValidServiceDomainEntity() {
        Category category = new Category("cat-almacenamiento", "almacenamiento", "Almacenamiento");
        Icon icon = new Icon("https://cdn.example.com/icons/amazon-efs.svg", "svg", "32x32");
        Pricing pricing = new Pricing(PricingModel.PAY_AS_YOU_GO, FreeTierType.CREDITS, "5 GB durante 12 meses");
        
        ServiceDetails details = new ServiceDetails(
                "Sistema de archivos administrado.",
                "https://aws.amazon.com/efs/",
                List.of("Almacenamiento compartido para EC2"),
                true,
                Scope.REGIONAL,
                pricing
        );

        Metadata metadata = new Metadata(true, ServiceStatus.ACTIVE, List.of("storage", "nfs"), Instant.now());

        Service service = Service.builder()
                .id("aws-srv-001")
                .slug(new ServiceSlug("amazon-efs"))
                .name("Amazon EFS")
                .type("service")
                .category(category)
                .icon(icon)
                .details(details)
                .metadata(metadata)
                .learning(new Learning(List.of(), List.of("CLF-C02"), List.of(), List.of()))
                .build();

        assertNotNull(service);
        assertEquals("aws-srv-001", service.getId());
        assertEquals("amazon-efs", service.getSlug().value());
        assertEquals("Almacenamiento", service.getCategory().name());
        assertEquals(Scope.REGIONAL, service.getDetails().scope());
        assertEquals(PricingModel.PAY_AS_YOU_GO, service.getDetails().pricing().model());
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException si faltan campos obligatorios")
    void shouldThrowExceptionWhenRequiredFieldsAreMissing() {
        assertThrows(IllegalArgumentException.class, () -> 
            Service.builder()
                .id(null) // ID nulo debe fallar
                .slug(new ServiceSlug("amazon-efs"))
                .name("Amazon EFS")
                .build()
        );
    }
}