package com.bcopstein.voos.repositorio;

import com.bcopstein.voos.dominio.Aeronave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AeronaveRepository extends JpaRepository<Aeronave, String> {
}
