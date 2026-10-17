package com.bcopstein.voos.adapter.out.persistence.adapter;

import com.bcopstein.voos.adapter.out.persistence.mapper.ReservaPersistenceMapper;
import com.bcopstein.voos.adapter.out.persistence.repository.ReservaJpaRepository;
import com.bcopstein.voos.application.exception.ConflitoDeReservaException;
import com.bcopstein.voos.application.port.out.ReservaPort;
import com.bcopstein.voos.domain.model.reserva.Reserva;
import com.bcopstein.voos.domain.model.tempo.Periodo;
import com.bcopstein.voos.domain.model.tempo.Slot;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
public class ReservaJpaAdapter implements ReservaPort {
    private final ReservaJpaRepository repository;

    public ReservaJpaAdapter(ReservaJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Reserva> buscarNoPeriodo(Periodo periodo) {
        return repository.findByInicioSlotGreaterThanEqualAndInicioSlotLessThan(Slot.contendo(periodo.inicio()).inicio(), periodo.fim())
                .stream().map(ReservaPersistenceMapper::toDomain).toList();
    }

    @Override
    public List<Reserva> buscarDoPlano(Long planoId) {
        return repository.findByPlanoIdOrderByInicioSlotAscIdAsc(planoId).stream().map(ReservaPersistenceMapper::toDomain).toList();
    }

    /** O flush força o banco a verificar as restrições UNIQUE ainda dentro da transação da aprovação. */
    @Override
    public void registrar(List<Reserva> reservas) {
        try {
            repository.saveAllAndFlush(reservas.stream().map(ReservaPersistenceMapper::toEntity).toList());
        } catch (DataIntegrityViolationException e) {
            throw new ConflitoDeReservaException(
                    "Um recurso do plano foi reservado por outro plano durante a aprovação. Nenhuma reserva foi mantida; o plano continua em edição.");
        }
    }

    @Override
    public void liberarDoPlano(Long planoId) {
        repository.deleteByPlanoId(planoId);
    }
}
