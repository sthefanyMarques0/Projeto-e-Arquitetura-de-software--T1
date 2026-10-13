package com.bcopstein.voos.repositorio;

import com.bcopstein.voos.dominio.Piloto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PilotoRepository extends JpaRepository<Piloto, Long> {
}
