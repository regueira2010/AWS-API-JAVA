package com.aws.dashboard.api.infrastructure.adapter.output.persistence;

import com.aws.dashboard.api.domain.model.Service;
import com.aws.dashboard.api.domain.model.ServiceSlug;
import com.aws.dashboard.api.domain.repository.ServiceRepository;
import com.aws.dashboard.api.infrastructure.adapter.output.persistence.mapper.ServiceEntityMapper;
import com.aws.dashboard.api.infrastructure.adapter.output.persistence.repository.SpringDataJpaServiceRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@Transactional(readOnly = true)
public class PostgresServiceRepository implements ServiceRepository {

    private final SpringDataJpaServiceRepository jpaRepository;

    public PostgresServiceRepository(SpringDataJpaServiceRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public Service save(Service service) {
        var entity = ServiceEntityMapper.toEntity(service);
        var savedEntity = jpaRepository.save(entity);
        return ServiceEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Service> findById(String id) {
        return jpaRepository.findById(id).map(ServiceEntityMapper::toDomain);
    }

    @Override
    public Optional<Service> findBySlug(ServiceSlug slug) {
        return jpaRepository.findBySlug(slug.value()).map(ServiceEntityMapper::toDomain);
    }

    @Override
    public List<Service> findAll() {
        return jpaRepository.findAll().stream()
                .map(ServiceEntityMapper::toDomain)
                .toList();
    }

    @Override
    public List<Service> findByCategoryId(String categoryId) {
        return List.of();
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public long countUniqueCategories() {
        return jpaRepository.countUniqueCategories();
    }
}