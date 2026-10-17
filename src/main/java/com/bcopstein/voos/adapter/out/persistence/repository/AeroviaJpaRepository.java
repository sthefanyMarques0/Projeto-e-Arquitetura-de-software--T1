package com.bcopstein.voos.adapter.out.persistence.repository;

import com.bcopstein.voos.adapter.out.persistence.entity.AeroviaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AeroviaJpaRepository extends JpaRepository<AeroviaEntity, String> {}
