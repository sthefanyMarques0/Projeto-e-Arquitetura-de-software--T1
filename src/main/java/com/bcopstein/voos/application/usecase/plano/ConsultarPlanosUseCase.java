package com.bcopstein.voos.application.usecase.plano;

import com.bcopstein.voos.application.port.in.ConsultarPlanos;
import com.bcopstein.voos.application.port.out.PlanoDeVooPort;
import com.bcopstein.voos.application.port.out.ReservaPort;
import com.bcopstein.voos.domain.model.plano.PlanoDeVoo;
import com.bcopstein.voos.domain.model.plano.SituacaoPlano;
import com.bcopstein.voos.domain.model.reserva.Reserva;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsultarPlanosUseCase implements ConsultarPlanos {
    private final ReferenciasDoPlano referencias;
    private final PlanoDeVooPort planos;
    private final ReservaPort reservas;

    public ConsultarPlanosUseCase(ReferenciasDoPlano referencias, PlanoDeVooPort planos, ReservaPort reservas) {
        this.referencias = referencias;
        this.planos = planos;
        this.reservas = reservas;
    }

    @Override
    @Transactional(readOnly = true)
    public PlanoDeVoo porId(Long planoId) {
        return referencias.plano(planoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanoDeVoo> porSituacao(SituacaoPlano situacao) {
        return situacao == null ? planos.listar() : planos.listarPorSituacao(situacao);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> reservasDoPlano(Long planoId) {
        referencias.plano(planoId);
        return reservas.buscarDoPlano(planoId);
    }
}
