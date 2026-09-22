package com.aws.dashboard.api.domain.repository;

import com.aws.dashboard.api.domain.model.Service;
import com.aws.dashboard.api.domain.model.ServiceSlug;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository {
    Service save(Service service);
    Optional<Service> findById(String id);
    Optional<Service> findBySlug(ServiceSlug slug);
    List<Service> findAll();
    List<Service> findByCategoryId(String categoryId);
    long count();
    long countUniqueCategories();
}
