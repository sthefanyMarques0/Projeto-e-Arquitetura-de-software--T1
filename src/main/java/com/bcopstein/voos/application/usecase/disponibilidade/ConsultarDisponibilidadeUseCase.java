package com.bcopstein.voos.application.usecase.disponibilidade;

import com.bcopstein.voos.application.port.in.ConsultarDisponibilidade;
import com.bcopstein.voos.application.port.out.ReservaPort;
import com.bcopstein.voos.application.usecase.plano.ReferenciasDoPlano;
import com.bcopstein.voos.domain.model.aeronave.Aeronave;
import com.bcopstein.voos.domain.model.tempo.Periodo;
import com.bcopstein.voos.domain.service.ServicoDisponibilidade;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Mostra a situação no momento da consulta. Não reserva nada. */
@Service
public class ConsultarDisponibilidadeUseCase implements ConsultarDisponibilidade {
    private final ReferenciasDoPlano referencias;
    private final ReservaPort reservas;
    private final ServicoDisponibilidade servicoDisponibilidade;

    public ConsultarDisponibilidadeUseCase(ReferenciasDoPlano referencias, ReservaPort reservas,
                                           ServicoDisponibilidade servicoDisponibilidade) {
        this.referencias = referencias;
        this.reservas = reservas;
        this.servicoDisponibilidade = servicoDisponibilidade;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> altitudesDisponiveis(String aeroviaId, LocalDateTime inicio, LocalDateTime fim, String prefixoAeronave) {
        Periodo periodo = new Periodo(inicio, fim);
        Aeronave aeronave = prefixoAeronave == null || prefixoAeronave.isBlank() ? null : referencias.aeronave(prefixoAeronave);
        return servicoDisponibilidade.altitudesLivres(referencias.aerovia(aeroviaId), periodo, aeronave,
                reservas.buscarNoPeriodo(periodo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> vagasDisponiveis(String aeroportoCodigo, LocalDateTime inicio, LocalDateTime fim) {
        Periodo periodo = new Periodo(inicio, fim);
        return servicoDisponibilidade.vagasLivres(referencias.aeroporto(aeroportoCodigo), periodo,
                reservas.buscarNoPeriodo(periodo));
    }
}
