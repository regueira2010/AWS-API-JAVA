package com.aws.dashboard.api.infrastructure.config;

import com.aws.dashboard.api.domain.model.*;
import com.aws.dashboard.api.domain.repository.ServiceRepository;
import com.aws.dashboard.api.infrastructure.adapter.output.persistence.entity.CategoryEntity;
import com.aws.dashboard.api.infrastructure.adapter.output.persistence.repository.SpringDataJpaCategoryRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.Instant;
import java.util.*;

@Component
public class DataDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataDataLoader.class);

    private final ServiceRepository serviceRepository;
    private final SpringDataJpaCategoryRepository categoryRepository;
    private final ObjectMapper objectMapper;

    public DataDataLoader(ServiceRepository serviceRepository,
                          SpringDataJpaCategoryRepository categoryRepository,
                          ObjectMapper objectMapper) {
        this.serviceRepository = serviceRepository;
        this.categoryRepository = categoryRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) {
        if (serviceRepository.count() > 0) {
            log.info("Catalog database already initialized with {} services.", serviceRepository.count());
            return;
        }

        log.info("Loading canonical AWS catalog from classpath:/aws_api_sanitized.json...");
        try (InputStream is = getClass().getResourceAsStream("/aws_api_sanitized.json")) {
            if (is == null) {
                log.warn("aws_api_sanitized.json not found on classpath!");
                return;
            }

            JsonNode root = objectMapper.readTree(is);
            JsonNode results = root.get("results");
            if (results == null || !results.isArray()) {
                log.warn("Invalid catalog format: 'results' array missing");
                return;
            }

            // 1. Ingest Categories first (deduplicated)
            Map<String, CategoryEntity> categoryMap = new HashMap<>();
            for (JsonNode sNode : results) {
                JsonNode catNode = sNode.get("category");
                if (catNode != null) {
                    String catId = catNode.path("id").asText();
                    if (!categoryMap.containsKey(catId)) {
                        CategoryEntity catEntity = new CategoryEntity(
                                catId,
                                catNode.path("slug").asText(),
                                catNode.path("name").asText()
                        );
                        categoryMap.put(catId, catEntity);
                    }
                }
            }
            categoryRepository.saveAll(categoryMap.values());
            log.info("Saved {} canonical AWS categories to database.", categoryMap.size());

            // 2. Ingest Services
            List<Service> servicesToSave = new ArrayList<>();
            for (JsonNode sNode : results) {
                String id = sNode.path("id").asText();
                String slug = sNode.path("slug").asText();
                String name = sNode.path("name").asText();
                String type = sNode.path("type").asText("service");

                JsonNode catNode = sNode.path("category");
                Category category = new Category(
                        catNode.path("id").asText(),
                        catNode.path("slug").asText(),
                        catNode.path("name").asText()
                );

                JsonNode iconNode = sNode.path("icon");
                Icon icon = new Icon(
                        iconNode.path("url").asText(""),
                        iconNode.path("format").asText("svg"),
                        iconNode.path("dimensions").asText("32x32")
                );

                JsonNode detailsNode = sNode.path("details");
                JsonNode pricingNode = detailsNode.path("pricing");
                Pricing pricing = new Pricing(
                        PricingModel.fromValue(pricingNode.path("model").asText("pay_as_you_go")),
                        FreeTierType.fromValue(pricingNode.path("free_tier_type").asText("none")),
                        pricingNode.path("limits_summary").asText("")
                );

                List<String> useCases = new ArrayList<>();
                if (detailsNode.has("use_cases") && detailsNode.get("use_cases").isArray()) {
                    for (JsonNode uc : detailsNode.get("use_cases")) {
                        useCases.add(uc.asText());
                    }
                }

                ServiceDetails details = new ServiceDetails(
                        detailsNode.path("short_summary").asText(null),
                        detailsNode.path("description").asText(""),
                        detailsNode.path("service_url").asText(null),
                        detailsNode.path("documentation_url").asText(null),
                        detailsNode.path("pricing_url").asText(null),
                        detailsNode.path("aws_doc_url").asText("https://aws.amazon.com/"),
                        useCases,
                        detailsNode.path("free_tier").asBoolean(false),
                        Scope.fromValue(detailsNode.path("scope").asText("regional")),
                        detailsNode.path("cli_namespace").asText(null),
                        detailsNode.path("deployment_model").asText("managed"),
                        pricing
                );

                JsonNode metaNode = sNode.path("metadata");
                List<String> tags = new ArrayList<>();
                if (metaNode.has("tags") && metaNode.get("tags").isArray()) {
                    for (JsonNode t : metaNode.get("tags")) {
                        tags.add(t.asText());
                    }
                }
                Instant updatedAt = Instant.now();
                if (metaNode.has("updated_at")) {
                    try {
                        updatedAt = Instant.parse(metaNode.path("updated_at").asText());
                    } catch (Exception ignored) {}
                }
                Metadata metadata = new Metadata(
                        metaNode.path("is_active").asBoolean(true),
                        ServiceStatus.fromValue(metaNode.path("status").asText("active")),
                        tags,
                        updatedAt
                );

                JsonNode learningNode = sNode.path("learning");
                List<Learning.Synergy> synergies = new ArrayList<>();
                if (learningNode.has("architectural_synergies") && learningNode.get("architectural_synergies").isArray()) {
                    for (JsonNode syn : learningNode.get("architectural_synergies")) {
                        synergies.add(new Learning.Synergy(
                                syn.path("service_slug").asText(),
                                syn.path("reason").asText()
                        ));
                    }
                }

                List<String> certCodes = new ArrayList<>();
                if (learningNode.has("certifications") && learningNode.get("certifications").isArray()) {
                    for (JsonNode c : learningNode.get("certifications")) {
                        certCodes.add(c.asText());
                    }
                }

                List<Learning.CertificationDetail> certDetails = new ArrayList<>();
                if (learningNode.has("certification_details") && learningNode.get("certification_details").isArray()) {
                    for (JsonNode cd : learningNode.get("certification_details")) {
                        certDetails.add(new Learning.CertificationDetail(
                                cd.path("code").asText(),
                                cd.path("name").asText()
                        ));
                    }
                }

                List<String> examTips = new ArrayList<>();
                if (learningNode.has("exam_tips") && learningNode.get("exam_tips").isArray()) {
                    for (JsonNode et : learningNode.get("exam_tips")) {
                        examTips.add(et.asText());
                    }
                }

                String certLevel = learningNode.path("primary_certification_level").asText("associate");

                Learning learning = new Learning(
                        synergies,
                        certCodes,
                        certDetails,
                        certLevel,
                        examTips
                );

                Service service = Service.builder()
                        .id(id)
                        .slug(new ServiceSlug(slug))
                        .name(name)
                        .type(type)
                        .category(category)
                        .icon(icon)
                        .details(details)
                        .metadata(metadata)
                        .learning(learning)
                        .build();

                servicesToSave.add(service);
            }

            for (Service s : servicesToSave) {
                serviceRepository.save(s);
            }
            log.info("Successfully ingested {} AWS services into PostgreSQL database!", servicesToSave.size());

        } catch (Exception e) {
            log.error("Failed to load catalog from aws_api_sanitized.json", e);
        }
    }
}