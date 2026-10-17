package com.bcopstein.voos.adapter.out.persistence.repository;

import com.bcopstein.voos.adapter.out.persistence.entity.PilotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PilotoJpaRepository extends JpaRepository<PilotoEntity, Long> {}
