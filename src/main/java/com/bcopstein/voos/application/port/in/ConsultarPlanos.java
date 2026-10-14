package com.bcopstein.voos.application.port.in;

import com.bcopstein.voos.domain.model.plano.PlanoDeVoo;
import com.bcopstein.voos.domain.model.plano.SituacaoPlano;
import com.bcopstein.voos.domain.model.reserva.Reserva;
import java.util.List;

public interface ConsultarPlanos {
    PlanoDeVoo porId(Long planoId);

    /** Lista os planos de uma situação; com situação nula, lista todos. */
    List<PlanoDeVoo> porSituacao(SituacaoPlano situacao);

    List<Reserva> reservasDoPlano(Long planoId);
}
