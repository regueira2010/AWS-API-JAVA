package com.aws.dashboard.api.domain.model;

public enum Scope {
    REGIONAL("regional"),
    GLOBAL("global"),
    ZONAL("zonal"),
    MULTI_SCOPE("multi_scope"),
    UNKNOWN("unknown");

    private final String value;

    Scope(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Scope fromValue(String text) {
        if (text == null) return UNKNOWN;
        for (Scope s : Scope.values()) {
            if (s.value.equalsIgnoreCase(text)) {
                return s;
            }
        }
        return UNKNOWN;
    }
}