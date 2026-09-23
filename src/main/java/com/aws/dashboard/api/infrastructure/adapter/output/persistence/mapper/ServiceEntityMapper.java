package com.aws.dashboard.api.infrastructure.adapter.output.persistence.mapper;

import com.aws.dashboard.api.domain.model.*;
import com.aws.dashboard.api.infrastructure.adapter.output.persistence.entity.CategoryEntity;
import com.aws.dashboard.api.infrastructure.adapter.output.persistence.entity.ServiceEntity;

import java.util.List;

public class ServiceEntityMapper {

    public static Service toDomain(ServiceEntity entity) {
        if (entity == null) return null;

        Category category = new Category(
                entity.getCategory().getId(),
                entity.getCategory().getSlug(),
                entity.getCategory().getName()
        );

        Icon icon = new Icon(
                entity.getIconUrl(),
                entity.getIconFormat(),
                entity.getIconDimensions()
        );

        Pricing pricing = new Pricing(
                PricingModel.fromValue(entity.getPricingModel()),
                FreeTierType.fromValue(entity.getPricingFreeTierType()),
                entity.getPricingLimitsSummary()
        );

        ServiceDetails details = new ServiceDetails(
                entity.getDescription(),
                entity.getAwsDocUrl(),
                entity.getUseCases() != null ? entity.getUseCases() : List.of(),
                entity.isFreeTier(),
                Scope.fromValue(entity.getScope()),
                pricing
        );

        Metadata metadata = new Metadata(
                entity.isActive(),
                ServiceStatus.fromValue(entity.getStatus()),
                entity.getTags() != null ? entity.getTags() : List.of(),
                entity.getUpdatedAt()
        );

        return Service.builder()
                .id(entity.getId())
                .slug(new ServiceSlug(entity.getSlug()))
                .name(entity.getName())
                .type(entity.getType())
                .category(category)
                .icon(icon)
                .details(details)
                .metadata(metadata)
                .learning(new Learning(List.of(), List.of(), List.of(), List.of()))
                .build();
    }

    public static ServiceEntity toEntity(Service domain) {
        if (domain == null) return null;

        CategoryEntity categoryEntity = CategoryEntity.builder()
                .id(domain.getCategory().id())
                .slug(domain.getCategory().slug())
                .name(domain.getCategory().name())
                .build();

        return ServiceEntity.builder()
                .id(domain.getId())
                .slug(domain.getSlug().value())
                .name(domain.getName())
                .type(domain.getType())
                .category(categoryEntity)
                .iconUrl(domain.getIcon().url())
                .iconFormat(domain.getIcon().format())
                .iconDimensions(domain.getIcon().dimensions())
                .description(domain.getDetails().description())
                .awsDocUrl(domain.getDetails().awsDocUrl())
                .freeTier(domain.getDetails().freeTier())
                .scope(domain.getDetails().scope().getValue())
                .pricingModel(domain.getDetails().pricing().model().getValue())
                .pricingFreeTierType(domain.getDetails().pricing().freeTierType().getValue())
                .pricingLimitsSummary(domain.getDetails().pricing().limitsSummary())
                .isActive(domain.getMetadata().isActive())
                .status(domain.getMetadata().status().getValue())
                .updatedAt(domain.getMetadata().updatedAt())
                .useCases(domain.getDetails().useCases())
                .tags(domain.getMetadata().tags())
                .build();
    }
}