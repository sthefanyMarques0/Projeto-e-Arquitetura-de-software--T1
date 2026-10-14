package com.bcopstein.voos.adapter.out.persistence.repository;

import com.bcopstein.voos.adapter.out.persistence.entity.PlanoDeVooEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanoDeVooJpaRepository extends JpaRepository<PlanoDeVooEntity, Long> {
    List<PlanoDeVooEntity> findBySituacaoOrderByIdAsc(String situacao);

    List<PlanoDeVooEntity> findAllByOrderByIdAsc();
}
