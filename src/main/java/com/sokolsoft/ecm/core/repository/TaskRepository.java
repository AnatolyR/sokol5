package com.sokolsoft.ecm.core.repository;

import com.sokolsoft.ecm.core.model.Document;
import com.sokolsoft.ecm.core.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.lang.Nullable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RepositoryRestResource
@Secured("ROLE_SYSTEM")
public interface TaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {
    @Secured({"ROLE_USER", "ROLE_SYSTEM"})
    @Override
    Task getOne(UUID uuid);

    @Secured({"ROLE_USER", "ROLE_SYSTEM"})
    @Override
    Page<Task> findAll(Pageable var1);

    @Secured({"ROLE_USER", "ROLE_SYSTEM"})
    @Override
    Page<Task> findAll(@Nullable Specification<Task> spec, Pageable pageable);

    @Secured({"ROLE_USER", "ROLE_SYSTEM"})
    @Override
    Optional<Task> findById(UUID uuid);
}
