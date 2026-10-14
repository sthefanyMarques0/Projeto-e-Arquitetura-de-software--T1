package com.bcopstein.voos.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TrechoResponse(
        int ordem,
        String aeroviaId,
        String origem,
        String destino,
        BigDecimal distanciaKm,
        int altitude,
        @JsonFormat(pattern = FormatoDataHora.PADRAO) LocalDateTime entradaPrevista,
        @JsonFormat(pattern = FormatoDataHora.PADRAO) LocalDateTime chegadaPrevista,
        long duracaoSegundos,
        String tipoPassagem,
        Integer permanenciaMinutos,
        Integer vagaPretendida) {}
