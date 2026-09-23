package com.aws.dashboard.api.infrastructure.config;

import com.aws.dashboard.api.domain.model.*;
import com.aws.dashboard.api.domain.repository.ServiceRepository;
import com.aws.dashboard.api.infrastructure.adapter.output.persistence.entity.CategoryEntity;
import com.aws.dashboard.api.infrastructure.adapter.output.persistence.repository.SpringDataJpaCategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class DataDataLoader implements CommandLineRunner {

    private final ServiceRepository serviceRepository;
    private final SpringDataJpaCategoryRepository categoryRepository;

    public DataDataLoader(ServiceRepository serviceRepository,
                          SpringDataJpaCategoryRepository categoryRepository) {
        this.serviceRepository = serviceRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        if (serviceRepository.count() == 0) {
            // 1. Guardar Categoría Semilla
            CategoryEntity categoryEntity = new CategoryEntity("cat-almacenamiento", "almacenamiento", "Almacenamiento");
            categoryRepository.save(categoryEntity);

            // 2. Guardar Servicio Semilla
            Service seedService = Service.builder()
                    .id("aws-srv-001")
                    .slug(new ServiceSlug("amazon-efs"))
                    .name("Amazon EFS")
                    .type("service")
                    .category(new Category("cat-almacenamiento", "almacenamiento", "Almacenamiento"))
                    .icon(new Icon("https://cdn.example.com/icons/amazon-efs.svg", "svg", "32x32"))
                    .details(new ServiceDetails(
                            "Sistema de archivos administrado y compartido.",
                            "https://aws.amazon.com/efs/",
                            List.of("Almacenamiento compartido para EC2"),
                            true,
                            Scope.REGIONAL,
                            new Pricing(PricingModel.PAY_AS_YOU_GO, FreeTierType.CREDITS, "Consultar AWS")
                    ))
                    .metadata(new Metadata(true, ServiceStatus.ACTIVE, List.of("storage", "nfs"), Instant.now()))
                    .learning(new Learning(List.of(), List.of("CLF-C02"), List.of(), List.of()))
                    .build();

            serviceRepository.save(seedService);
        }
    }
}