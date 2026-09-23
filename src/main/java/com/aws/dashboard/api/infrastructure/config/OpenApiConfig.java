package com.aws.dashboard.api.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AWS Cloud Services Encyclopedia API")
                        .version("1.1.0")
                        .description("""
                                ### Enciclopedia Oficial de Servicios de Amazon Web Services (AWS)
                                
                                API RESTful de alta disponibilidad, baja latencia y grado de producción diseñada para consulta, 
                                exploración y enriquecimiento del catálogo canónico de servicios en la nube de AWS.
                                
                                #### Características Principales:
                                - **Catálogo Completo:** 280 servicios oficiales categorizados en 22 taxonomías formales de AWS.
                                - **Certificaciones AWS:** Cobertura de las 13 certificaciones oficiales vigentes (desde Foundational hasta Specialty y Professional).
                                - **Arquitectura y Rendimiento:** Implementación Hexagonal / DDD con caché L1 en memoria (Caffeine), soporte condicional HTTP ETag (`304 Not Modified`) y compresión Gzip.
                                - **Seguridad y Resiliencia:** Rate limiting por IP (120 req/min mediante Bucket4j) y cabeceras OWASP integradas.
                                """)
                        .contact(new Contact()
                                .name("AWS API Architecture Team")
                                .url("https://github.com/regueira2010/AWS-API-JAVA")
                                .email("architecture@aws-catalog.local"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Entorno Local de Desarrollo"),
                        new Server().url("https://api.aws-services.local").description("Gateway de Producción")
                ))
                .tags(List.of(
                        new Tag().name("Servicios AWS").description("Operaciones de consulta, paginación y búsqueda detallada de servicios en la nube."),
                        new Tag().name("Observabilidad").description("Monitoreo de salud, métricas de Prometheus y sondas de Kubernetes.")
                ));
    }
}
