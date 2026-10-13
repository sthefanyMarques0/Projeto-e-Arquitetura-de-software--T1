package com.bcopstein.voos.repositorio;

import com.bcopstein.voos.dominio.Aeroporto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AeroportoRepository extends JpaRepository<Aeroporto, String> {
}
