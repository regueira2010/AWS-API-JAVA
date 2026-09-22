package com.aws.dashboard.api.domain.model;

import java.util.List;

public record Learning(
        List<Synergy> architecturalSynergies,
        List<String> certifications,
        List<CertificationDetail> certificationDetails,
        List<String> examTips
) {
    public static record Synergy(String serviceSlug, String reason) {}
    public static record CertificationDetail(String code, String name) {}

    public Learning {
        if (architecturalSynergies == null) architecturalSynergies = List.of();
        if (certifications == null) certifications = List.of();
        if (certificationDetails == null) certificationDetails = List.of();
        if (examTips == null) examTips = List.of();
    }
}