package com.aws.dashboard.api.domain.model;

public enum FreeTierType {
    NONE("none"),
    ALWAYS_FREE("always_free"),
    FREE_TRIAL("free_trial"),
    CREDITS("credits"),
    SERVICE_FREE_TIER("service_free_tier"),
    TWELVE_MONTHS_FREE("twelve_months_free"),
    UNKNOWN("unknown");

    private final String value;

    FreeTierType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static FreeTierType fromValue(String text) {
        if (text == null) return UNKNOWN;
        for (FreeTierType ft : FreeTierType.values()) {
            if (ft.value.equalsIgnoreCase(text)) {
                return ft;
            }
        }
        return UNKNOWN;
    }
}