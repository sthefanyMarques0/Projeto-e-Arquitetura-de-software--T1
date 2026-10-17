package com.bcopstein.voos.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReservaResponse(
        Long id,
        Long planoId,
        String tipo,
        String aeroviaId,
        Integer altitude,
        String aeroportoCodigo,
        Integer vaga,
        @JsonFormat(pattern = FormatoDataHora.PADRAO) LocalDateTime inicioSlot,
        @JsonFormat(pattern = FormatoDataHora.PADRAO) LocalDateTime fimSlot) {}
