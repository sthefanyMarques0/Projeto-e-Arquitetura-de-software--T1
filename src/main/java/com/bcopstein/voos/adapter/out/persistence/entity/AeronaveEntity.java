package com.bcopstein.voos.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "aeronaves")
public class AeronaveEntity {
    @Id
    private String prefixo;
    private String categoria;
    @Column(name = "velocidade_cruzeiro_km_h")
    private BigDecimal velocidadeCruzeiroKmH;
    private BigDecimal autonomiaNominalKm;
    private String empresaManutencao;
    private String companhiaAerea;
    private Integer capacidadeMaximaPassageiros;
    private BigDecimal capacidadeMaximaToneladas;

    protected AeronaveEntity() {}

    public String getPrefixo() { return prefixo; }
    public String getCategoria() { return categoria; }
    public BigDecimal getVelocidadeCruzeiroKmH() { return velocidadeCruzeiroKmH; }
    public BigDecimal getAutonomiaNominalKm() { return autonomiaNominalKm; }
    public String getEmpresaManutencao() { return empresaManutencao; }
    public String getCompanhiaAerea() { return companhiaAerea; }
    public Integer getCapacidadeMaximaPassageiros() { return capacidadeMaximaPassageiros; }
    public BigDecimal getCapacidadeMaximaToneladas() { return capacidadeMaximaToneladas; }
}
