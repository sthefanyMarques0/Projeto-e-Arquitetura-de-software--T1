package com.bcopstein.voos.application.port.out;

import com.bcopstein.voos.domain.model.plano.PlanoDeVoo;
import com.bcopstein.voos.domain.model.plano.SituacaoPlano;
import java.util.List;
import java.util.Optional;

public interface PlanoDeVooPort {
    Optional<PlanoDeVoo> buscar(Long id);

    List<PlanoDeVoo> listar();

    List<PlanoDeVoo> listarPorSituacao(SituacaoPlano situacao);

    /** Grava o plano (novo ou existente) e devolve a versão persistida, com identificador. */
    PlanoDeVoo salvar(PlanoDeVoo plano);
}
