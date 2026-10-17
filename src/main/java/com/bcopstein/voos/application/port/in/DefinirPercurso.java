package com.bcopstein.voos.application.port.in;

import com.bcopstein.voos.domain.model.plano.PlanoDeVoo;
import java.util.List;

public interface DefinirPercurso {
    PlanoDeVoo definir(Long planoId, List<TrechoCommand> trechos);
}
