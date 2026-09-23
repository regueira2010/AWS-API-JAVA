package com.aws.dashboard.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.aws.dashboard.api.infrastructure.adapter.output.persistence.repository")
public class AwsServicesApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AwsServicesApiApplication.class, args);
    }
}