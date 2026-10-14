package com.bcopstein.voos.domain.model.aeronave;

import com.bcopstein.voos.domain.model.tempo.Periodo;
import java.math.BigDecimal;
import java.util.Set;

public class AeronaveParticular extends Aeronave {
    private static final Set<Integer> ALTITUDES_PERMITIDAS = Set.of(25_000, 26_000, 27_000);

    private final String empresaManutencao;

    public AeronaveParticular(String prefixo, BigDecimal velocidadeCruzeiroKmH, BigDecimal autonomiaNominalKm, String empresaManutencao) {
        super(prefixo, velocidadeCruzeiroKmH, autonomiaNominalKm);
        this.empresaManutencao = empresaManutencao;
    }

    @Override
    public CategoriaAeronave getCategoria() { return CategoriaAeronave.PARTICULAR; }

    @Override
    public boolean permiteOperar(int altitudePes, Periodo periodo) {
        return ALTITUDES_PERMITIDAS.contains(altitudePes);
    }

    @Override
    public String descricaoRestricao() {
        return "aeronave particular opera somente a 25.000, 26.000 ou 27.000 pés";
    }

    public String getEmpresaManutencao() { return empresaManutencao; }
}
