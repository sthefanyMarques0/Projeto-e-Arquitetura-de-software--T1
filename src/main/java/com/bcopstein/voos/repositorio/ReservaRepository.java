package com.bcopstein.voos.repositorio;

import com.bcopstein.voos.dominio.Aeroporto;
import com.bcopstein.voos.dominio.Aerovia;
import com.bcopstein.voos.dominio.PlanoDeVoo;
import com.bcopstein.voos.dominio.Reserva;
import com.bcopstein.voos.dominio.TipoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    void deleteByPlano(PlanoDeVoo plano);

    List<Reserva> findByAeroviaAndAltitudeAndSlotInicioLessThanAndSlotFimGreaterThan(
            Aerovia aerovia, int altitude, LocalDateTime fim, LocalDateTime inicio);

    List<Reserva> findByAeroportoAndVagaAndSlotInicioLessThanAndSlotFimGreaterThan(
            Aeroporto aeroporto, int vaga, LocalDateTime fim, LocalDateTime inicio);

    List<Reserva> findByAeroportoAndSlotInicioLessThanAndSlotFimGreaterThan(
            Aeroporto aeroporto, LocalDateTime fim, LocalDateTime inicio);

    List<Reserva> findByAeroviaAndSlotInicioLessThanAndSlotFimGreaterThan(
            Aerovia aerovia, LocalDateTime fim, LocalDateTime inicio);
}
