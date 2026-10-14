package com.bcopstein.voos.application.port.in;

import com.bcopstein.voos.domain.model.plano.ResultadoValidacao;

public interface ValidarPlano {
    ResultadoValidacao validar(Long planoId);
}
