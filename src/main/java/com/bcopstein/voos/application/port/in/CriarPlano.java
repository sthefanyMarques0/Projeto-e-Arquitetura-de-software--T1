package com.bcopstein.voos.application.port.in;

import com.bcopstein.voos.domain.model.plano.PlanoDeVoo;

public interface CriarPlano {
    PlanoDeVoo criar(DadosPlanoCommand dados);
}
