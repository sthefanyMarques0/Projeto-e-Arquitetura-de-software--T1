package com.bcopstein.voos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record NovoPlanoRequest(
    @NotNull Long pilotoId,
    @NotBlank String prefixoAeronave,
    @NotBlank String origemCodigo,
    @NotBlank String destinoCodigo,
    @NotNull LocalDateTime partidaPrevista
) {}
