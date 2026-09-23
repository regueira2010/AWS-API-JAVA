package com.aws.dashboard.api.infrastructure;

import com.aws.dashboard.api.application.dto.CatalogEnvelopeDTO;
import com.aws.dashboard.api.application.dto.ServiceResponseDTO;
import com.aws.dashboard.api.infrastructure.config.JacksonConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SerializationTest {

    @Test
    public void testSerialization() throws Exception {
        ObjectMapper mapper = new JacksonConfig().objectMapper();

        ServiceResponseDTO.CategoryDTO cat = new ServiceResponseDTO.CategoryDTO("cat-1", "compute", "Compute");
        ServiceResponseDTO.IconDTO icon = new ServiceResponseDTO.IconDTO("https://icon.url", "svg", "64x64");
        ServiceResponseDTO.PricingDTO pricing = new ServiceResponseDTO.PricingDTO("pay-as-you-go", "always_free", "Free tier limits");
        ServiceResponseDTO.DetailsDTO details = new ServiceResponseDTO.DetailsDTO(
                "Short summary", "Description", "https://srv.url", "https://doc.url", "https://price.url",
                "https://doc.url", List.of("use-case-1"), true, "regional", "ec2", "managed", pricing
        );
        ServiceResponseDTO.MetadataDTO metadata = new ServiceResponseDTO.MetadataDTO(true, "active", List.of("tag1"), "2026-09-23T00:00:00Z");
        ServiceResponseDTO.LearningDTO learning = new ServiceResponseDTO.LearningDTO(
                List.of(new ServiceResponseDTO.LearningDTO.SynergyDTO("s3", "Storage synergy")),
                List.of("SAA-C03"),
                List.of(new ServiceResponseDTO.LearningDTO.CertificationDetailDTO("SAA-C03", "Solutions Architect")),
                "associate",
                List.of("Exam tip 1")
        );

        ServiceResponseDTO service = new ServiceResponseDTO(
                "aws-srv-001", "amazon-ec2", "Amazon EC2", "service",
                cat, icon, details, metadata, learning
        );

        CatalogEnvelopeDTO.InfoDTO info = new CatalogEnvelopeDTO.InfoDTO(
                "AWS Services API", "1.0.0", "1.1.0", "2026.09", "AWS", 1, 1, "2026-09-23T00:00:00Z", "pipeline"
        );
        CatalogEnvelopeDTO.PaginationDTO pagination = new CatalogEnvelopeDTO.PaginationDTO(
                1, 1, 1, 1, false, null, null, null
        );

        CatalogEnvelopeDTO envelope = new CatalogEnvelopeDTO(info, pagination, List.of(service));

        String json = mapper.writeValueAsString(envelope);
        System.out.println("SERIALIZED JSON: " + json);
        assertNotNull(json);
    }
}
