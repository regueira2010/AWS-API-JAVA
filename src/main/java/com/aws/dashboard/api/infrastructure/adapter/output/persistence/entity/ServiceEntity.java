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

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "aws_doc_url", nullable = false)
    private String awsDocUrl;

    @Column(name = "free_tier", nullable = false)
    private boolean freeTier;

    @Column(nullable = false)
    private String scope;

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

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "service_use_cases", joinColumns = @JoinColumn(name = "service_id"))
    @Column(name = "use_case", columnDefinition = "TEXT")
    private List<String> useCases = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "service_tags", joinColumns = @JoinColumn(name = "service_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();
}
