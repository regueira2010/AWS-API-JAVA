package com.aws.dashboard.api.application.dto;

import com.aws.dashboard.api.domain.model.Service;

import java.time.format.DateTimeFormatter;

public class ServiceDTOMapper {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    public static ServiceResponseDTO toDTO(Service domain) {
        if (domain == null) return null;

        var categoryDTO = new ServiceResponseDTO.CategoryDTO(
                domain.getCategory().id(),
                domain.getCategory().slug(),
                domain.getCategory().name()
        );

        var iconDTO = new ServiceResponseDTO.IconDTO(
                domain.getIcon().url(),
                domain.getIcon().format(),
                domain.getIcon().dimensions()
        );

        var pricingDTO = new ServiceResponseDTO.PricingDTO(
                domain.getDetails().pricing().model().getValue(),
                domain.getDetails().pricing().freeTierType().getValue(),
                domain.getDetails().pricing().limitsSummary()
        );

        var detailsDTO = new ServiceResponseDTO.DetailsDTO(
                domain.getDetails().shortSummary(),
                domain.getDetails().description(),
                domain.getDetails().serviceUrl(),
                domain.getDetails().documentationUrl(),
                domain.getDetails().pricingUrl(),
                domain.getDetails().awsDocUrl(),
                domain.getDetails().useCases(),
                domain.getDetails().freeTier(),
                domain.getDetails().scope().getValue(),
                domain.getDetails().cliNamespace(),
                domain.getDetails().deploymentModel(),
                pricingDTO
        );

        var metadataDTO = new ServiceResponseDTO.MetadataDTO(
                domain.getMetadata().isActive(),
                domain.getMetadata().status().getValue(),
                domain.getMetadata().tags(),
                domain.getMetadata().updatedAt() != null ? ISO_FORMATTER.format(domain.getMetadata().updatedAt()) : null
        );

        var synergies = domain.getLearning().architecturalSynergies().stream()
                .map(s -> new ServiceResponseDTO.LearningDTO.SynergyDTO(s.serviceSlug(), s.reason()))
                .toList();

        var certDetails = domain.getLearning().certificationDetails().stream()
                .map(c -> new ServiceResponseDTO.LearningDTO.CertificationDetailDTO(c.code(), c.name()))
                .toList();

        var learningDTO = new ServiceResponseDTO.LearningDTO(
                synergies,
                domain.getLearning().certifications(),
                certDetails,
                domain.getLearning().primaryCertificationLevel(),
                domain.getLearning().examTips()
        );

        return new ServiceResponseDTO(
                domain.getId(),
                domain.getSlug().value(),
                domain.getName(),
                domain.getType(),
                categoryDTO,
                iconDTO,
                detailsDTO,
                metadataDTO,
                learningDTO
        );
    }
}
