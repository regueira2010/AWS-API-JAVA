package com.aws.dashboard.api.infrastructure.adapter.input.rest;

import com.aws.dashboard.api.application.dto.CatalogEnvelopeDTO;
import com.aws.dashboard.api.application.dto.ServiceResponseDTO;
import com.aws.dashboard.api.application.usecase.GetCatalogEnvelopeUseCase;
import com.aws.dashboard.api.application.usecase.GetServiceBySlugUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/services")
public class ServiceRestController {

    private final GetCatalogEnvelopeUseCase getCatalogEnvelopeUseCase;
    private final GetServiceBySlugUseCase getServiceBySlugUseCase;

    public ServiceRestController(GetCatalogEnvelopeUseCase getCatalogEnvelopeUseCase,
                                 GetServiceBySlugUseCase getServiceBySlugUseCase) {
        this.getCatalogEnvelopeUseCase = getCatalogEnvelopeUseCase;
        this.getServiceBySlugUseCase = getServiceBySlugUseCase;
    }

    @GetMapping
    public ResponseEntity<CatalogEnvelopeDTO> getCatalog() {
        CatalogEnvelopeDTO envelope = getCatalogEnvelopeUseCase.execute();
        return ResponseEntity.ok(envelope);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ServiceResponseDTO> getServiceBySlug(@PathVariable String slug) {
        ServiceResponseDTO service = getServiceBySlugUseCase.execute(slug);
        if (service == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(service);
    }
}
