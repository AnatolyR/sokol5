package com.sokolsoft.ecm.core.repository;

import com.sokolsoft.ecm.core.model.Authority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, UUID> {

    Page<Authority> findByUsernameEquals(@Param("username") String username, Pageable p);

    List<Authority> findAllByUsernameEquals(@Param("username") String username);
}
