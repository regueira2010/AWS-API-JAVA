package com.aws.dashboard.api.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableCaching
public class WebConfig implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);
    private final ObjectMapper objectMapper;

    public WebConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public ShallowEtagHeaderFilter shallowEtagHeaderFilter() {
        return new ShallowEtagHeaderFilter();
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("Configuring primary Jackson 2 message converter after ByteArrayHttpMessageConverter...");
        converters.removeIf(c -> c instanceof MappingJackson2HttpMessageConverter);

        int byteConverterIdx = -1;
        for (int i = 0; i < converters.size(); i++) {
            if (converters.get(i) instanceof org.springframework.http.converter.ByteArrayHttpMessageConverter) {
                byteConverterIdx = i;
                break;
            }
        }

        var jacksonConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        if (byteConverterIdx >= 0) {
            converters.add(byteConverterIdx + 1, jacksonConverter);
        } else {
            converters.add(0, jacksonConverter);
            converters.add(0, new org.springframework.http.converter.ByteArrayHttpMessageConverter());
        }
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "OPTIONS")
                .allowedHeaders("Content-Type", "Accept", "If-None-Match", "Authorization")
                .maxAge(3600);
    }
}