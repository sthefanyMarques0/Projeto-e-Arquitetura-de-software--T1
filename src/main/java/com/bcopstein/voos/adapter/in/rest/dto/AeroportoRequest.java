package com.bcopstein.voos.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record AeroportoRequest(
        @NotBlank @Size(max = 10) String codigo,
        @NotBlank String nome,
        @NotNull @PositiveOrZero Integer vagas) {}
