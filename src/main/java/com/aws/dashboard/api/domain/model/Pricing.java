package com.aws.dashboard.api.domain.model;

public record Pricing(PricingModel model, FreeTierType freeTierType, String limitsSummary) {
    public Pricing {
        if (model == null) model = PricingModel.UNKNOWN;
        if (freeTierType == null) freeTierType = FreeTierType.UNKNOWN;
    }
}
