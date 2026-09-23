-- V1__create_aws_catalog_schema.sql
-- Creación de esquema para el catálogo de servicios de AWS

-- 1. Tabla de Categorías
CREATE TABLE categories (
    id VARCHAR(50) PRIMARY KEY,
    slug VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL
);

-- 2. Tabla Principal de Servicios AWS
CREATE TABLE services (
    id VARCHAR(100) PRIMARY KEY,
    slug VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    type VARCHAR(20) NOT NULL DEFAULT 'service',
    category_id VARCHAR(50) NOT NULL,
    
    -- Icono
    icon_url TEXT NOT NULL,
    icon_format VARCHAR(10) NOT NULL DEFAULT 'svg',
    icon_dimensions VARCHAR(20),
    
    -- Detalles
    short_summary TEXT,
    description TEXT NOT NULL,
    service_url TEXT,
    documentation_url TEXT,
    pricing_url TEXT,
    aws_doc_url TEXT NOT NULL,
    free_tier BOOLEAN NOT NULL DEFAULT false,
    scope VARCHAR(30) NOT NULL DEFAULT 'regional',
    cli_namespace VARCHAR(50),
    deployment_model VARCHAR(30) NOT NULL DEFAULT 'managed',
    primary_certification_level VARCHAR(50) NOT NULL DEFAULT 'associate',
    
    -- Precios (subobjeto pricing)
    pricing_model VARCHAR(50) NOT NULL DEFAULT 'pay_as_you_go',
    pricing_free_tier_type VARCHAR(50) NOT NULL DEFAULT 'none',
    pricing_limits_summary TEXT,
    
    -- Metadatos
    is_active BOOLEAN NOT NULL DEFAULT true,
    status VARCHAR(30) NOT NULL DEFAULT 'active',
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_services_category FOREIGN KEY (category_id) REFERENCES categories (id)
);

-- 3. Tabla de Casos de Uso (Relación 1 a N)
CREATE TABLE service_use_cases (
    id BIGSERIAL PRIMARY KEY,
    service_id VARCHAR(100) NOT NULL,
    use_case TEXT NOT NULL,
    CONSTRAINT fk_use_cases_service FOREIGN KEY (service_id) REFERENCES services (id) ON DELETE CASCADE
);

-- 4. Tabla de Tags (Relación 1 a N)
CREATE TABLE service_tags (
    id BIGSERIAL PRIMARY KEY,
    service_id VARCHAR(100) NOT NULL,
    tag VARCHAR(50) NOT NULL,
    CONSTRAINT fk_tags_service FOREIGN KEY (service_id) REFERENCES services (id) ON DELETE CASCADE
);

-- 5. Tabla de Sinergias Arquitectónicas
CREATE TABLE service_synergies (
    id BIGSERIAL PRIMARY KEY,
    service_id VARCHAR(100) NOT NULL,
    target_service_slug VARCHAR(100) NOT NULL,
    reason TEXT NOT NULL,
    CONSTRAINT fk_synergies_service FOREIGN KEY (service_id) REFERENCES services (id) ON DELETE CASCADE
);

-- 6. Tabla de Certificaciones AWS
CREATE TABLE service_certifications (
    id BIGSERIAL PRIMARY KEY,
    service_id VARCHAR(100) NOT NULL,
    code VARCHAR(20) NOT NULL,
    name VARCHAR(150) NOT NULL,
    CONSTRAINT fk_certifications_service FOREIGN KEY (service_id) REFERENCES services (id) ON DELETE CASCADE
);

-- 7. Tabla de Tips de Examen
CREATE TABLE service_exam_tips (
    id BIGSERIAL PRIMARY KEY,
    service_id VARCHAR(100) NOT NULL,
    tip TEXT NOT NULL,
    CONSTRAINT fk_exam_tips_service FOREIGN KEY (service_id) REFERENCES services (id) ON DELETE CASCADE
);

-- Índices para optimizar búsquedas frecuentes
CREATE INDEX idx_services_slug ON services (slug);
CREATE INDEX idx_services_category_id ON services (category_id);
CREATE INDEX idx_services_status ON services (status);
CREATE INDEX idx_service_tags_tag ON service_tags (tag);