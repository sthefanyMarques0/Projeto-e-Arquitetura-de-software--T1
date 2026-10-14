package com.bcopstein.voos.adapter.out.persistence.repository;

import com.bcopstein.voos.adapter.out.persistence.entity.ReservaEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservaJpaRepository extends JpaRepository<ReservaEntity, Long> {
    List<ReservaEntity> findByInicioSlotGreaterThanEqualAndInicioSlotLessThan(LocalDateTime de, LocalDateTime ate);

    List<ReservaEntity> findByPlanoIdOrderByInicioSlotAscIdAsc(Long planoId);

    @Modifying
    @Query("delete from ReservaEntity r where r.planoId = :planoId")
    int deleteByPlanoId(@Param("planoId") Long planoId);
}
