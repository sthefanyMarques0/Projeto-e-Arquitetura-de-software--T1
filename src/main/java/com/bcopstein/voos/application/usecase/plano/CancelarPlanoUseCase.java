package com.bcopstein.voos.application.usecase.plano;

import com.bcopstein.voos.application.port.in.CancelarPlano;
import com.bcopstein.voos.application.port.out.PlanoDeVooPort;
import com.bcopstein.voos.application.port.out.RelogioPort;
import com.bcopstein.voos.application.port.out.ReservaPort;
import com.bcopstein.voos.domain.model.plano.PlanoDeVoo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Cancela um plano aprovado futuro e libera todas as suas reservas, numa única transação. */
@Service
public class CancelarPlanoUseCase implements CancelarPlano {
    private final ReferenciasDoPlano referencias;
    private final PlanoDeVooPort planos;
    private final ReservaPort reservas;
    private final RelogioPort relogio;

    public CancelarPlanoUseCase(ReferenciasDoPlano referencias, PlanoDeVooPort planos, ReservaPort reservas,
                                RelogioPort relogio) {
        this.referencias = referencias;
        this.planos = planos;
        this.reservas = reservas;
        this.relogio = relogio;
    }

    @Override
    @Transactional
    public PlanoDeVoo cancelar(Long planoId) {
        PlanoDeVoo plano = referencias.plano(planoId);
        plano.cancelar(relogio.agora());
        reservas.liberarDoPlano(planoId);
        return planos.salvar(plano);
    }
}
