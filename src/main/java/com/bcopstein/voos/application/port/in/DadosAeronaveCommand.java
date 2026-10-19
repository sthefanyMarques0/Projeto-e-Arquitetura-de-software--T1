package com.bcopstein.voos.application.port.in;

import com.bcopstein.voos.domain.model.aeronave.CategoriaAeronave;
import java.math.BigDecimal;

/** Dados de cadastro de uma aeronave. Os campos específicos usados dependem da categoria. */
public record DadosAeronaveCommand(String prefixo, CategoriaAeronave categoria, BigDecimal velocidadeCruzeiroKmH,
                                   BigDecimal autonomiaNominalKm, String empresaManutencao, String companhiaAerea,
                                   Integer capacidadeMaximaPassageiros, BigDecimal capacidadeMaximaToneladas) {}
