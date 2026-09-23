-- V2__add_performance_indexes.sql
-- Índices para optimización de queries y relaciones en alta concurrencia

CREATE INDEX IF NOT EXISTS idx_services_category_scope ON services (category_id, scope);
CREATE INDEX IF NOT EXISTS idx_services_free_tier ON services (free_tier);
CREATE INDEX IF NOT EXISTS idx_services_cli_namespace ON services (cli_namespace);
CREATE INDEX IF NOT EXISTS idx_service_synergies_target ON service_synergies (target_service_slug);
CREATE INDEX IF NOT EXISTS idx_service_certifications_code ON service_certifications (code);
