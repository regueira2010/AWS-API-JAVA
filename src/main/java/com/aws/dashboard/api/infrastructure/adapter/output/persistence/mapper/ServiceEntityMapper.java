package com.aws.dashboard.api.infrastructure.adapter.output.persistence.mapper;

import com.aws.dashboard.api.domain.model.*;
import com.aws.dashboard.api.infrastructure.adapter.output.persistence.entity.CategoryEntity;
import com.aws.dashboard.api.infrastructure.adapter.output.persistence.entity.ServiceEntity;

import java.util.ArrayList;
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
                entity.getShortSummary(),
                entity.getDescription(),
                entity.getServiceUrl(),
                entity.getDocumentationUrl(),
                entity.getPricingUrl(),
                entity.getAwsDocUrl(),
                entity.getUseCases() != null ? new java.util.ArrayList<>(entity.getUseCases()) : List.of(),
                entity.isFreeTier(),
                Scope.fromValue(entity.getScope()),
                entity.getCliNamespace(),
                entity.getDeploymentModel(),
                pricing
        );

        Metadata metadata = new Metadata(
                entity.isActive(),
                ServiceStatus.fromValue(entity.getStatus()),
                entity.getTags() != null ? new java.util.ArrayList<>(entity.getTags()) : List.of(),
                entity.getUpdatedAt()
        );

        var synergies = entity.getSynergies() != null
                ? entity.getSynergies().stream()
                        .map(s -> new Learning.Synergy(s.getTargetServiceSlug(), s.getReason()))
                        .toList()
                : List.<Learning.Synergy>of();

        var certDetails = entity.getCertifications() != null
                ? entity.getCertifications().stream()
                        .map(c -> new Learning.CertificationDetail(c.getCode(), c.getName()))
                        .toList()
                : List.<Learning.CertificationDetail>of();

        var certCodes = certDetails.stream().map(Learning.CertificationDetail::code).toList();
        var examTips = entity.getExamTips() != null ? new java.util.ArrayList<>(entity.getExamTips()) : List.<String>of();

        Learning learning = new Learning(
                synergies,
                certCodes,
                certDetails,
                entity.getPrimaryCertificationLevel(),
                examTips
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
                .learning(learning)
                .build();
    }

    public static ServiceEntity toEntity(Service domain) {
        if (domain == null) return null;

        CategoryEntity categoryEntity = CategoryEntity.builder()
                .id(domain.getCategory().id())
                .slug(domain.getCategory().slug())
                .name(domain.getCategory().name())
                .build();

        var synergies = domain.getLearning() != null && domain.getLearning().architecturalSynergies() != null
                ? domain.getLearning().architecturalSynergies().stream()
                        .map(s -> new ServiceEntity.SynergyEmbeddable(s.serviceSlug(), s.reason()))
                        .toList()
                : List.<ServiceEntity.SynergyEmbeddable>of();

        var certifications = domain.getLearning() != null && domain.getLearning().certificationDetails() != null
                ? domain.getLearning().certificationDetails().stream()
                        .map(c -> new ServiceEntity.CertificationEmbeddable(c.code(), c.name()))
                        .toList()
                : List.<ServiceEntity.CertificationEmbeddable>of();

        var examTips = domain.getLearning() != null && domain.getLearning().examTips() != null
                ? domain.getLearning().examTips()
                : List.<String>of();

        return ServiceEntity.builder()
                .id(domain.getId())
                .slug(domain.getSlug().value())
                .name(domain.getName())
                .type(domain.getType())
                .category(categoryEntity)
                .iconUrl(domain.getIcon().url())
                .iconFormat(domain.getIcon().format())
                .iconDimensions(domain.getIcon().dimensions())
                .shortSummary(domain.getDetails().shortSummary())
                .description(domain.getDetails().description())
                .serviceUrl(domain.getDetails().serviceUrl())
                .documentationUrl(domain.getDetails().documentationUrl())
                .pricingUrl(domain.getDetails().pricingUrl())
                .awsDocUrl(domain.getDetails().awsDocUrl())
                .freeTier(domain.getDetails().freeTier())
                .scope(domain.getDetails().scope().getValue())
                .cliNamespace(domain.getDetails().cliNamespace())
                .deploymentModel(domain.getDetails().deploymentModel())
                .primaryCertificationLevel(domain.getLearning() != null ? domain.getLearning().primaryCertificationLevel() : null)
                .pricingModel(domain.getDetails().pricing().model().getValue())
                .pricingFreeTierType(domain.getDetails().pricing().freeTierType().getValue())
                .pricingLimitsSummary(domain.getDetails().pricing().limitsSummary())
                .isActive(domain.getMetadata().isActive())
                .status(domain.getMetadata().status().getValue())
                .updatedAt(domain.getMetadata().updatedAt())
                .useCases(new ArrayList<>(domain.getDetails().useCases()))
                .tags(new ArrayList<>(domain.getMetadata().tags()))
                .synergies(new ArrayList<>(synergies))
                .certifications(new ArrayList<>(certifications))
                .examTips(new ArrayList<>(examTips))
                .build();
    }
}