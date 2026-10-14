package com.bcopstein.voos.domain.model.aeronave;

import java.math.BigDecimal;

public abstract class AeronaveComercial extends Aeronave {
    private final String companhiaAerea;

    protected AeronaveComercial(String prefixo, BigDecimal velocidadeCruzeiroKmH, BigDecimal autonomiaNominalKm, String companhiaAerea) {
        super(prefixo, velocidadeCruzeiroKmH, autonomiaNominalKm);
        this.companhiaAerea = companhiaAerea;
    }

    public String getCompanhiaAerea() { return companhiaAerea; }
}
