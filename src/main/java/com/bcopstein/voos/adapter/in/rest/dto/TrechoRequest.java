package com.bcopstein.voos.adapter.in.rest.dto;

import com.bcopstein.voos.domain.model.plano.TipoPassagem;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/** Trecho informado pelo usuário. A chegada é calculada pelo sistema e não deve ser enviada. */
public record TrechoRequest(
        @NotBlank String aeroviaId,
        @NotNull Integer altitude,
        @NotNull @JsonFormat(pattern = FormatoDataHora.PADRAO) LocalDateTime entradaPrevista,
        @NotNull TipoPassagem tipoPassagem,
        Integer permanenciaMinutos,
        Integer vagaPretendida) {}
