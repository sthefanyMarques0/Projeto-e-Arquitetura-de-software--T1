package com.bcopstein.voos.application.port.in;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsultarDisponibilidade {
    /** Altitudes livres na aerovia em todo o período; se a aeronave for informada, aplica as regras da categoria. */
    List<Integer> altitudesDisponiveis(String aeroviaId, LocalDateTime inicio, LocalDateTime fim, String prefixoAeronave);

    /** Números das vagas livres no aeroporto em todo o período. */
    List<Integer> vagasDisponiveis(String aeroportoCodigo, LocalDateTime inicio, LocalDateTime fim);
}
