package com.aws.dashboard.api.application.dto;

import java.util.List;

public record ServiceResponseDTO(
        String id,
        String slug,
        String name,
        String type,
        CategoryDTO category,
        IconDTO icon,
        DetailsDTO details,
        MetadataDTO metadata,
        LearningDTO learning
) {
    public record CategoryDTO(String id, String slug, String name) {}
    public record IconDTO(String url, String format, String dimensions) {}
    
    public record DetailsDTO(
            String shortSummary,
            String description,
            String serviceUrl,
            String documentationUrl,
            String pricingUrl,
            String awsDocUrl,
            List<String> useCases,
            boolean freeTier,
            String scope,
            String cliNamespace,
            String deploymentModel,
            PricingDTO pricing
    ) {
        public DetailsDTO(
                String description,
                String awsDocUrl,
                List<String> useCases,
                boolean freeTier,
                String scope,
                PricingDTO pricing
        ) {
            this(null, description, null, awsDocUrl, null, awsDocUrl, useCases, freeTier, scope, null, null, pricing);
        }
    }
    
    public record PricingDTO(String model, String freeTierType, String limitsSummary) {}
    
    public record MetadataDTO(
            boolean isActive,
            String status,
            List<String> tags,
            String updatedAt
    ) {}
    
    public record LearningDTO(
            List<SynergyDTO> architecturalSynergies,
            List<String> certifications,
            List<CertificationDetailDTO> certificationDetails,
            String primaryCertificationLevel,
            List<String> examTips
    ) {
        public LearningDTO(
                List<SynergyDTO> architecturalSynergies,
                List<String> certifications,
                List<CertificationDetailDTO> certificationDetails,
                List<String> examTips
        ) {
            this(architecturalSynergies, certifications, certificationDetails, null, examTips);
        }

        public record SynergyDTO(String serviceSlug, String reason) {}
        public record CertificationDetailDTO(String code, String name) {}
    }
}