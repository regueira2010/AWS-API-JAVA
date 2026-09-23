package com.aws.dashboard.api.infrastructure.adapter.output.persistence.repository;

import com.aws.dashboard.api.infrastructure.adapter.output.persistence.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataJpaServiceRepository extends JpaRepository<ServiceEntity, String> {
    Optional<ServiceEntity> findBySlug(String slug);

    @Query("SELECT COUNT(DISTINCT s.category.id) FROM ServiceEntity s")
    long countUniqueCategories();
}