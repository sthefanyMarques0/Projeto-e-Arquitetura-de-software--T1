package com.bcopstein.voos.adapter.in.rest.dto;

import com.bcopstein.voos.domain.model.aeronave.CategoriaAeronave;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record AeronaveRequest(
        @NotBlank String prefixo,
        @NotNull CategoriaAeronave categoria,
        @NotNull @Positive BigDecimal velocidadeCruzeiroKmH,
        @NotNull @Positive BigDecimal autonomiaNominalKm,
        String empresaManutencao,
        String companhiaAerea,
        Integer capacidadeMaximaPassageiros,
        BigDecimal capacidadeMaximaToneladas) {}
