package com.sokolsoft.ecm.core.repository;

import com.sokolsoft.ecm.core.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
//@RepositoryRestResource
//@Secured("ROLE_SYSTEM")
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

    @Secured({"ROLE_USER", "ROLE_SYSTEM"})
    @Override
    <S extends Task> List<S> saveAll(Iterable<S> entities);

    @Secured({"ROLE_USER", "ROLE_SYSTEM"})
    List<Task> findAllByDocumentId(UUID documentId);

    @Secured({"ROLE_USER", "ROLE_SYSTEM"})
    Task findOneByDocumentIdAndId(UUID documentId, UUID taskId);
}
