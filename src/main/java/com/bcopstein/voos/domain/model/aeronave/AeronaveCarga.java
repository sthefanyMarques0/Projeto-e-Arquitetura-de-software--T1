package com.bcopstein.voos.domain.model.aeronave;

import com.bcopstein.voos.domain.model.tempo.Periodo;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AeronaveCarga extends AeronaveComercial {
    private static final LocalTime FIM_JANELA = LocalTime.of(6, 0);

    private final BigDecimal capacidadeMaximaToneladas;

    public AeronaveCarga(String prefixo, BigDecimal velocidadeCruzeiroKmH, BigDecimal autonomiaNominalKm, String companhiaAerea, BigDecimal capacidadeMaximaToneladas) {
        super(prefixo, velocidadeCruzeiroKmH, autonomiaNominalKm, companhiaAerea);
        this.capacidadeMaximaToneladas = capacidadeMaximaToneladas;
    }

    @Override
    public CategoriaAeronave getCategoria() { return CategoriaAeronave.COMERCIAL_CARGA; }

    /** Todo o período [inicio, fim) deve estar contido em [00:00, 06:00) do dia do início. */
    @Override
    public boolean permiteOperar(int altitudePes, Periodo periodo) {
        LocalDateTime limite = periodo.inicio().toLocalDate().atTime(FIM_JANELA);
        return periodo.inicio().isBefore(limite) && !periodo.fim().isAfter(limite);
    }

    @Override
    public String descricaoRestricao() {
        return "aeronave comercial de carga opera somente entre 00:00 (inclusive) e 06:00 (exclusive)";
    }

    public BigDecimal getCapacidadeMaximaToneladas() { return capacidadeMaximaToneladas; }
}
