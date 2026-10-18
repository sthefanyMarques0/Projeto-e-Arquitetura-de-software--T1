package com.bcopstein.voos.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "aerovias")
public class AeroviaEntity {
    @Id
    private String id;
    @Column(name = "origem_codigo")
    private String origemCodigo;
    @Column(name = "destino_codigo")
    private String destinoCodigo;
    @Column(name = "distancia_km")
    private BigDecimal distanciaKm;

    protected AeroviaEntity() {}

    public AeroviaEntity(String id, String origemCodigo, String destinoCodigo, BigDecimal distanciaKm) {
        this.id = id;
        this.origemCodigo = origemCodigo;
        this.destinoCodigo = destinoCodigo;
        this.distanciaKm = distanciaKm;
    }

    public String getId() { return id; }
    public String getOrigemCodigo() { return origemCodigo; }
    public String getDestinoCodigo() { return destinoCodigo; }
    public BigDecimal getDistanciaKm() { return distanciaKm; }
}
