package com.aws.dashboard.api.infrastructure.adapter.input.rest;

import com.aws.dashboard.api.application.dto.CatalogEnvelopeDTO;
import com.aws.dashboard.api.application.dto.ServiceResponseDTO;
import com.aws.dashboard.api.application.usecase.GetCatalogEnvelopeUseCase;
import com.aws.dashboard.api.application.usecase.GetServiceBySlugUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/services")
@Tag(name = "Servicios AWS", description = "Operaciones de consulta, paginación y búsqueda detallada de servicios en la nube.")
public class ServiceRestController {

    private final GetCatalogEnvelopeUseCase getCatalogEnvelopeUseCase;
    private final GetServiceBySlugUseCase getServiceBySlugUseCase;

    public ServiceRestController(GetCatalogEnvelopeUseCase getCatalogEnvelopeUseCase,
                                 GetServiceBySlugUseCase getServiceBySlugUseCase) {
        this.getCatalogEnvelopeUseCase = getCatalogEnvelopeUseCase;
        this.getServiceBySlugUseCase = getServiceBySlugUseCase;
    }

    @GetMapping
    @Operation(
            summary = "Obtener catálogo paginado de servicios de AWS",
            description = """
                    Retorna un sobre (envelope) canónico que incluye:
                    - **info**: Metadatos globales de versión, fecha de actualización y proveedores.
                    - **pagination**: Conteo total (280 servicios), páginas totales, enlaces `next` y `prev`.
                    - **results**: Lista de servicios con detalles, precios, modelos de despliegue y certificaciones.
                    
                    **Cabeceras de Soporte:**
                    - Emite cabecera `ETag` para validación condicional (`If-None-Match`).
                    - Habilita compresión Gzip para payloads grandes.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Catálogo recuperado exitosamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CatalogEnvelopeDTO.class))
            ),
            @ApiResponse(
                    responseCode = "304",
                    description = "No modificado (el contenido en caché del cliente coincide con el ETag actual).",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Límite de tasa excedido (máximo 120 peticiones por minuto por IP).",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor.",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<CatalogEnvelopeDTO> getCatalog(
            @Parameter(description = "Número de página (1-indexed). Por defecto 1.", example = "1")
            @RequestParam(required = false) Integer page,
            @Parameter(description = "Cantidad de elementos por página. Por defecto todos los elementos.", example = "20")
            @RequestParam(required = false) Integer limit) {
        CatalogEnvelopeDTO envelope = getCatalogEnvelopeUseCase.execute(page, limit);
        return ResponseEntity.ok(envelope);
    }

    @GetMapping("/{slug}")
    @Operation(
            summary = "Obtener un servicio de AWS por su slug canónico",
            description = """
                    Recupera la ficha técnica completa de un servicio de AWS a partir de su slug único (ej. `amazon-s3`, `amazon-elastic-compute-cloud`, `aws-lambda`).
                    
                    Incluye:
                    - URLs oficiales (servicio, documentación y precios).
                    - Modelo de precios y Free Tier oficial.
                    - Sinergias arquitectónicas recomendadas (Well-Architected).
                    - Mapeo hacia las 13 certificaciones oficiales de AWS (`CLF-C02`, `SAA-C03`, `SAP-C02`, etc.).
                    - Tips técnicos de examen.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Servicio encontrado exitosamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "304",
                    description = "No modificado (ETag coincide).",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Servicio no encontrado con el slug indicado.",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Límite de tasa excedido (Bucket4j).",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor.",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<ServiceResponseDTO> getServiceBySlug(
            @Parameter(description = "Slug identificador del servicio (formato kebab-case)", example = "amazon-simple-storage-service")
            @PathVariable String slug) {
        ServiceResponseDTO service = getServiceBySlugUseCase.execute(slug);
        if (service == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(service);
    }
}
