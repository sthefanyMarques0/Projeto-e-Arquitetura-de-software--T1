package com.bcopstein.voos.adapter.out.persistence.repository;

import com.bcopstein.voos.adapter.out.persistence.entity.AeroportoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AeroportoJpaRepository extends JpaRepository<AeroportoEntity, String> {}
