package com.aws.dashboard.api.domain.model;

public enum PricingModel {
    PAY_AS_YOU_GO("pay_as_you_go"),
    FREE("free"),
    PROVISIONED("provisioned"),
    BYOL("byol"),
    SUBSCRIPTION("subscription"),
    PER_REQUEST("per_request"),
    PER_UNIT("per_unit"),
    TIERED("tiered"),
    RESERVED("reserved"),
    MIXED("mixed"),
    UNKNOWN("unknown");

    private final String value;

    PricingModel(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PricingModel fromValue(String text) {
        if (text == null) return UNKNOWN;
        for (PricingModel pm : PricingModel.values()) {
            if (pm.value.equalsIgnoreCase(text)) {
                return pm;
            }
        }
        return UNKNOWN;
    }
}