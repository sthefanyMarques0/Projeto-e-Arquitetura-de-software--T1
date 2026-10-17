package com.bcopstein.voos.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PlanoResponse(
        Long id,
        String situacao,
        Long pilotoId,
        String pilotoNome,
        String prefixoAeronave,
        String categoriaAeronave,
        String origem,
        String destino,
        @JsonFormat(pattern = FormatoDataHora.PADRAO) LocalDateTime partidaPrevista,
        @JsonFormat(pattern = FormatoDataHora.PADRAO) LocalDateTime chegadaPrevistaDestino,
        List<TrechoResponse> trechos) {}
