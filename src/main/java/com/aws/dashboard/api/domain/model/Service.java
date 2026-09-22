package com.aws.dashboard.api.domain.model;

public class Service {
    private final String id;
    private final ServiceSlug slug;
    private final String name;
    private final String type;
    private final Category category;
    private final Icon icon;
    private final ServiceDetails details;
    private final Metadata metadata;
    private final Learning learning;

    private Service(Builder builder) {
        if (builder.id == null || builder.id.isBlank()) throw new IllegalArgumentException("El id no puede ser nulo o vacio");
        if (builder.slug == null) throw new IllegalArgumentException("El slug es obligatorio");
        if (builder.name == null || builder.name.isBlank()) throw new IllegalArgumentException("El nombre es obligatorio");
        if (builder.category == null) throw new IllegalArgumentException("La categoria es obligatoria");
        if (builder.details == null) throw new IllegalArgumentException("Los detalles son obligatorios");
        if (builder.metadata == null) throw new IllegalArgumentException("La metadata es obligatoria");

        this.id = builder.id;
        this.slug = builder.slug;
        this.name = builder.name;
        this.type = builder.type != null ? builder.type : "service";
        this.category = builder.category;
        this.icon = builder.icon;
        this.details = builder.details;
        this.metadata = builder.metadata;
        this.learning = builder.learning != null ? builder.learning : new Learning(null, null, null, null);
    }

    public String getId() { return id; }
    public ServiceSlug getSlug() { return slug; }
    public String getName() { return name; }
    public String getType() { return type; }
    public Category getCategory() { return category; }
    public Icon getIcon() { return icon; }
    public ServiceDetails getDetails() { return details; }
    public Metadata getMetadata() { return metadata; }
    public Learning getLearning() { return learning; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private ServiceSlug slug;
        private String name;
        private String type;
        private Category category;
        private Icon icon;
        private ServiceDetails details;
        private Metadata metadata;
        private Learning learning;

        public Builder id(String id) { this.id = id; return this; }
        public Builder slug(ServiceSlug slug) { this.slug = slug; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder type(String type) { this.type = type; return this; }
        public Builder category(Category category) { this.category = category; return this; }
        public Builder icon(Icon icon) { this.icon = icon; return this; }
        public Builder details(ServiceDetails details) { this.details = details; return this; }
        public Builder metadata(Metadata metadata) { this.metadata = metadata; return this; }
        public Builder learning(Learning learning) { this.learning = learning; return this; }

        public Service build() {
            return new Service(this);
        }
    }
}