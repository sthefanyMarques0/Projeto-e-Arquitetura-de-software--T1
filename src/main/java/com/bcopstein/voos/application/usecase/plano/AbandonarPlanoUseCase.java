package com.bcopstein.voos.application.usecase.plano;

import com.bcopstein.voos.application.port.in.AbandonarPlano;
import com.bcopstein.voos.application.port.out.PlanoDeVooPort;
import com.bcopstein.voos.domain.model.plano.PlanoDeVoo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Plano em edição não possui reservas: abandonar apenas muda a situação. */
@Service
public class AbandonarPlanoUseCase implements AbandonarPlano {
    private final ReferenciasDoPlano referencias;
    private final PlanoDeVooPort planos;

    public AbandonarPlanoUseCase(ReferenciasDoPlano referencias, PlanoDeVooPort planos) {
        this.referencias = referencias;
        this.planos = planos;
    }

    @Override
    @Transactional
    public PlanoDeVoo abandonar(Long planoId) {
        PlanoDeVoo plano = referencias.plano(planoId);
        plano.abandonar();
        return planos.salvar(plano);
    }
}
