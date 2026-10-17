package com.bcopstein.voos.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record PlanoRequest(
        @NotNull Long pilotoId,
        @NotBlank String prefixoAeronave,
        @NotBlank String origem,
        @NotBlank String destino,
        @NotNull @JsonFormat(pattern = FormatoDataHora.PADRAO) LocalDateTime partidaPrevista) {}
