package com.aws.dashboard.api.infrastructure.adapter.output.persistence.repository;

import com.aws.dashboard.api.infrastructure.adapter.output.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataJpaCategoryRepository extends JpaRepository<CategoryEntity, String> {
}