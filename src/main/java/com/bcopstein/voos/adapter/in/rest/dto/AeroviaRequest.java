package com.bcopstein.voos.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record AeroviaRequest(
        @NotBlank @Size(max = 20) String id,
        @NotBlank String origem,
        @NotBlank String destino,
        @NotNull @Positive BigDecimal distanciaKm) {}
