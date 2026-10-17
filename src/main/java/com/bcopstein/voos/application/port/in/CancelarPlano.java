package com.bcopstein.voos.application.port.in;

import com.bcopstein.voos.domain.model.plano.PlanoDeVoo;

public interface CancelarPlano {
    PlanoDeVoo cancelar(Long planoId);
}
