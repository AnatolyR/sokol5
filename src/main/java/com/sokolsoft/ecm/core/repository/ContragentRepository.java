package com.sokolsoft.ecm.core.repository;

import com.sokolsoft.ecm.core.model.Contragent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContragentRepository extends JpaRepository<Contragent, UUID> {

}
