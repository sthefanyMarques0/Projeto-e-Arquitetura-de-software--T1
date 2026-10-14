package com.bcopstein.voos.application.port.in;

import com.bcopstein.voos.domain.model.plano.TipoPassagem;
import java.time.LocalDateTime;

/** Trecho informado pelo usuário. A chegada não é informada: é calculada pelo sistema. */
public record TrechoCommand(String aeroviaId, int altitude, LocalDateTime entradaPrevista, TipoPassagem tipoPassagem,
                            Integer permanenciaMinutos, Integer vagaPretendida) {}
