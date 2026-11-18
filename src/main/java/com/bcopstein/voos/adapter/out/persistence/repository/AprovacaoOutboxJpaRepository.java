package com.bcopstein.voos.adapter.out.persistence.repository;

import com.bcopstein.voos.adapter.out.persistence.entity.AprovacaoOutboxEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AprovacaoOutboxJpaRepository extends JpaRepository<AprovacaoOutboxEntity, String> {
    List<AprovacaoOutboxEntity> findTop50ByPublicadoEmIsNullOrderByCriadoEmAsc();
}
