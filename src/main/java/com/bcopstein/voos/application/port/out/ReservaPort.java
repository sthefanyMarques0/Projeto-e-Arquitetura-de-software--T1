package com.bcopstein.voos.application.port.out;

import com.bcopstein.voos.application.exception.ConflitoDeReservaException;
import com.bcopstein.voos.domain.model.reserva.Reserva;
import com.bcopstein.voos.domain.model.tempo.Periodo;
import java.util.List;

public interface ReservaPort {
    /** Reservas cujos slots começam dentro do período (ajustado à hora cheia). */
    List<Reserva> buscarNoPeriodo(Periodo periodo);

    List<Reserva> buscarDoPlano(Long planoId);

    /**
     * Grava todas as reservas. Se algum recurso já estiver reservado no mesmo slot,
     * lança {@link ConflitoDeReservaException} (o banco garante a unicidade).
     */
    void registrar(List<Reserva> reservas);

    void liberarDoPlano(Long planoId);
}
