package com.aws.dashboard.api.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceEntity {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @Column(name = "icon_url", nullable = false)
    private String iconUrl;

    @Column(name = "icon_format", nullable = false)
    private String iconFormat;

    @Column(name = "icon_dimensions")
    private String iconDimensions;

    @Column(name = "short_summary", columnDefinition = "TEXT")
    private String shortSummary;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "service_url")
    private String serviceUrl;

    @Column(name = "documentation_url")
    private String documentationUrl;

    @Column(name = "pricing_url")
    private String pricingUrl;

    @Column(name = "aws_doc_url", nullable = false)
    private String awsDocUrl;

    @Column(name = "free_tier", nullable = false)
    private boolean freeTier;

    @Column(nullable = false)
    private String scope;

    @Column(name = "cli_namespace")
    private String cliNamespace;

    @Column(name = "deployment_model")
    private String deploymentModel;

    @Column(name = "primary_certification_level")
    private String primaryCertificationLevel;

    @Column(name = "pricing_model", nullable = false)
    private String pricingModel;

    @Column(name = "pricing_free_tier_type", nullable = false)
    private String pricingFreeTierType;

    @Column(name = "pricing_limits_summary", columnDefinition = "TEXT")
    private String pricingLimitsSummary;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(nullable = false)
    private String status;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @org.hibernate.annotations.BatchSize(size = 50)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "service_use_cases", joinColumns = @JoinColumn(name = "service_id"))
    @Column(name = "use_case", columnDefinition = "TEXT")
    @Builder.Default
    private List<String> useCases = new ArrayList<>();

    @org.hibernate.annotations.BatchSize(size = 50)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "service_tags", joinColumns = @JoinColumn(name = "service_id"))
    @Column(name = "tag")
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @org.hibernate.annotations.BatchSize(size = 50)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "service_synergies", joinColumns = @JoinColumn(name = "service_id"))
    @Builder.Default
    private List<SynergyEmbeddable> synergies = new ArrayList<>();

    @org.hibernate.annotations.BatchSize(size = 50)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "service_certifications", joinColumns = @JoinColumn(name = "service_id"))
    @Builder.Default
    private List<CertificationEmbeddable> certifications = new ArrayList<>();

    @org.hibernate.annotations.BatchSize(size = 50)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "service_exam_tips", joinColumns = @JoinColumn(name = "service_id"))
    @Column(name = "tip", columnDefinition = "TEXT")
    @Builder.Default
    private List<String> examTips = new ArrayList<>();

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SynergyEmbeddable {
        @Column(name = "target_service_slug", nullable = false)
        private String targetServiceSlug;
        @Column(name = "reason", columnDefinition = "TEXT", nullable = false)
        private String reason;
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CertificationEmbeddable {
        @Column(name = "code", nullable = false)
        private String code;
        @Column(name = "name", nullable = false)
        private String name;
    }
}
