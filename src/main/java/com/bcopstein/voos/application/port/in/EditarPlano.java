package com.bcopstein.voos.application.port.in;

import com.bcopstein.voos.domain.model.plano.PlanoDeVoo;

public interface EditarPlano {
    PlanoDeVoo editar(Long planoId, DadosPlanoCommand dados);
}
