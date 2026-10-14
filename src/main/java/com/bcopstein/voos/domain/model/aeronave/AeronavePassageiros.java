package com.bcopstein.voos.domain.model.aeronave;

import com.bcopstein.voos.domain.model.tempo.Periodo;
import java.math.BigDecimal;

public class AeronavePassageiros extends AeronaveComercial {
    private static final int ALTITUDE_MINIMA = 28_000;

    private final int capacidadeMaximaPassageiros;

    public AeronavePassageiros(String prefixo, BigDecimal velocidadeCruzeiroKmH, BigDecimal autonomiaNominalKm, String companhiaAerea, int capacidadeMaximaPassageiros) {
        super(prefixo, velocidadeCruzeiroKmH, autonomiaNominalKm, companhiaAerea);
        this.capacidadeMaximaPassageiros = capacidadeMaximaPassageiros;
    }

    @Override
    public CategoriaAeronave getCategoria() { return CategoriaAeronave.COMERCIAL_PASSAGEIROS; }

    @Override
    public boolean permiteOperar(int altitudePes, Periodo periodo) {
        return altitudePes >= ALTITUDE_MINIMA;
    }

    @Override
    public String descricaoRestricao() {
        return "aeronave comercial de passageiros opera somente a 28.000 pés ou mais";
    }

    public int getCapacidadeMaximaPassageiros() { return capacidadeMaximaPassageiros; }
}
