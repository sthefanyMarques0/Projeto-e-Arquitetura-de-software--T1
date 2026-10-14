package com.bcopstein.voos.adapter.out.persistence.repository;

import com.bcopstein.voos.adapter.out.persistence.entity.AeronaveEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AeronaveJpaRepository extends JpaRepository<AeronaveEntity, String> {}
