src/
├─ main/
│  ├─ java/
│  │  └─ com/
│  │     └─ aws/
│  │        └─ dashboard/
│  │           └─ api/
│  │              ├─ application/
│  │              │  ├─ dto/
│  │              │  │  ├─ CatalogEnvelopeDTO.java
│  │              │  │  ├─ ServiceDTOMapper.java
│  │              │  │  └─ ServiceResponseDTO.java
│  │              │  └─ usecase/
│  │              │     ├─ GetCatalogEnvelopeUseCase.java
│  │              │     └─ GetServiceBySlugUseCase.java
│  │              ├─ domain/
│  │              │  ├─ exception/
│  │              │  ├─ model/
│  │              │  │  ├─ Category.java
│  │              │  │  ├─ FreeTierType.java
│  │              │  │  ├─ Icon.java
│  │              │  │  ├─ Learning.java
│  │              │  │  ├─ Metadata.java
│  │              │  │  ├─ Pricing.java
│  │              │  │  ├─ PricingModel.java
│  │              │  │  ├─ Scope.java
│  │              │  │  ├─ Service.java
│  │              │  │  ├─ ServiceDetails.java
│  │              │  │  ├─ ServiceSlug.java
│  │              │  │  └─ ServiceStatus.java
│  │              │  └─ repository/
│  │              │     └─ ServiceRepository.java
│  │              ├─ infrastructure/
│  │              │  ├─ adapter/
│  │              │  │  ├─ input/
│  │              │  │  │  └─ rest/
│  │              │  │  │     └─ ServiceRestController.java
│  │              │  │  └─ output/
│  │              │  │     └─ persistence/
│  │              │  │        ├─ entity/
│  │              │  │        │  ├─ CategoryEntity.java
│  │              │  │        │  └─ ServiceEntity.java
│  │              │  │        ├─ mapper/
│  │              │  │        │  └─ ServiceEntityMapper.java
│  │              │  │        ├─ repository/
│  │              │  │        │  ├─ SpringDataJpaCategoryRepository.java
│  │              │  │        │  └─ SpringDataJpaServiceRepository.java
│  │              │  │        └─ PostgresServiceRepository.java
│  │              │  └─ config/
│  │              │     ├─ DataDataLoader.java
│  │              │     ├─ UseCaseConfig.java
│  │              │     └─ WebConfig.java
│  │              └─ AwsServicesApiApplication.java
│  └─ resources/
│     ├─ db/
│     │  └─ migration/
│     │     └─ migration/
│     │        └─ V1__create_aws_catalog_schema.sql
│     └─ application.yml
└─ test/
   └─ java/
      └─ com/
         └─ aws/
            └─ dashboard/
               └─ api/
                  ├─ application/
                  │  └─ usecase/
                  │     ├─ GetCatalogEnvelopeUseCaseTest.java
                  │     └─ GetServiceBySlugUseCaseTest.java
                  ├─ domain/
                  │  └─ model/
                  │     ├─ ServiceSlugTest.java
                  │     └─ ServiceTest.java
                  ├─ infrastructure/
                  ├─ AwsServicesApiApplicationTests.java
                  └─ TestcontainersConfiguration.java
