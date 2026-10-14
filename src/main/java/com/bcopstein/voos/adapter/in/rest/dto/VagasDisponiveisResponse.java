package com.bcopstein.voos.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;

public record VagasDisponiveisResponse(
        String aeroportoCodigo,
        @JsonFormat(pattern = FormatoDataHora.PADRAO) LocalDateTime inicio,
        @JsonFormat(pattern = FormatoDataHora.PADRAO) LocalDateTime fim,
        int quantidadeDisponivel,
        List<Integer> vagasDisponiveis) {}
