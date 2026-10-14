package com.bcopstein.voos.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AeronaveResponse(String prefixo, String categoria,
        BigDecimal velocidadeCruzeiroKmH, BigDecimal autonomiaNominalKm,
        String empresaManutencao, String companhiaAerea,
        Integer capacidadeMaximaPassageiros, BigDecimal capacidadeMaximaToneladas) {}
