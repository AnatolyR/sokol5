package com.sokolsoft.ecm.core.repository;

import com.sokolsoft.ecm.core.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
}
