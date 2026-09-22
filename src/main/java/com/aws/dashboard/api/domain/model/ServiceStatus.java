package com.aws.dashboard.api.domain.model;

public enum ServiceStatus {
    ACTIVE("active"),
    PREVIEW("preview"),
    DEPRECATED("deprecated"),
    RETIRED("retired"),
    UNKNOWN("unknown");

    private final String value;

    ServiceStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ServiceStatus fromValue(String text) {
        if (text == null) return UNKNOWN;
        for (ServiceStatus status : ServiceStatus.values()) {
            if (status.value.equalsIgnoreCase(text)) {
                return status;
            }
        }
        return UNKNOWN;
    }
}