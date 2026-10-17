package com.bcopstein.voos.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Uma linha por recurso e slot. As restrições UNIQUE da tabela (ver V1__criar_esquema.sql)
 * impedem que a mesma altitude ou vaga seja reservada duas vezes no mesmo slot.
 */
@Entity
@Table(name = "reservas")
public class ReservaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "plano_id", nullable = false)
    private Long planoId;
    @Column(name = "tipo", nullable = false)
    private String tipo;
    @Column(name = "aerovia_id")
    private String aeroviaId;
    @Column(name = "altitude")
    private Integer altitude;
    @Column(name = "aeroporto_codigo")
    private String aeroportoCodigo;
    @Column(name = "vaga")
    private Integer vaga;
    @Column(name = "inicio_slot", nullable = false)
    private LocalDateTime inicioSlot;

    protected ReservaEntity() {}

    public ReservaEntity(Long planoId, String tipo, String aeroviaId, Integer altitude, String aeroportoCodigo,
                         Integer vaga, LocalDateTime inicioSlot) {
        this.planoId = planoId;
        this.tipo = tipo;
        this.aeroviaId = aeroviaId;
        this.altitude = altitude;
        this.aeroportoCodigo = aeroportoCodigo;
        this.vaga = vaga;
        this.inicioSlot = inicioSlot;
    }

    public Long getId() { return id; }
    public Long getPlanoId() { return planoId; }
    public String getTipo() { return tipo; }
    public String getAeroviaId() { return aeroviaId; }
    public Integer getAltitude() { return altitude; }
    public String getAeroportoCodigo() { return aeroportoCodigo; }
    public Integer getVaga() { return vaga; }
    public LocalDateTime getInicioSlot() { return inicioSlot; }
}
