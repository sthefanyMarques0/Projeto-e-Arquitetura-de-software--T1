package com.bcopstein.voos.application.port.out;

import com.bcopstein.voos.domain.model.plano.PlanoDeVoo;
import java.time.LocalDateTime;

public interface AprovacaoOutboxPort {
    void registrar(String eventoId, PlanoDeVoo plano, LocalDateTime aprovadoEm);

    boolean contem(String eventoId);
}
