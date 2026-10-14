package com.bcopstein.voos.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AltitudesDisponiveisResponse(
        String aeroviaId,
        @JsonFormat(pattern = FormatoDataHora.PADRAO) LocalDateTime inicio,
        @JsonFormat(pattern = FormatoDataHora.PADRAO) LocalDateTime fim,
        String prefixoAeronave,
        List<Integer> altitudesDisponiveis) {}
