package com.bcopstein.voos.repositorio;

import com.bcopstein.voos.dominio.Aeroporto;
import com.bcopstein.voos.dominio.Aerovia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AeroviaRepository extends JpaRepository<Aerovia, String> {
    List<Aerovia> findByOrigemAndDestino(Aeroporto origem, Aeroporto destino);
}
