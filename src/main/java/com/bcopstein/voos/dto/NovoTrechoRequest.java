package com.bcopstein.voos.dto;

import com.bcopstein.voos.dominio.TipoPassagem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record NovoTrechoRequest(
    @NotBlank String aeroviaId,
    @NotNull Integer altitude,
    @NotNull LocalDateTime entradaPrevista,
    @NotNull TipoPassagem tipoPassagem,
    int tempoPermanencia,
    int vagaPretendida
) {}
