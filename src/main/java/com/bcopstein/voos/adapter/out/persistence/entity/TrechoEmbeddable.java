package com.bcopstein.voos.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;

/** Trecho gravado na tabela "trechos", como parte do agregado do plano (chave: plano_id + ordem). */
@Embeddable
public class TrechoEmbeddable {
    @Column(name = "aerovia_id", nullable = false)
    private String aeroviaId;
    @Column(name = "altitude", nullable = false)
    private Integer altitude;
    @Column(name = "entrada_prevista", nullable = false)
    private LocalDateTime entradaPrevista;
    @Column(name = "chegada_prevista", nullable = false)
    private LocalDateTime chegadaPrevista;
    @Column(name = "tipo_passagem", nullable = false)
    private String tipoPassagem;
    @Column(name = "permanencia_minutos")
    private Integer permanenciaMinutos;
    @Column(name = "vaga_pretendida")
    private Integer vagaPretendida;

    protected TrechoEmbeddable() {}

    public TrechoEmbeddable(String aeroviaId, Integer altitude, LocalDateTime entradaPrevista, LocalDateTime chegadaPrevista,
                            String tipoPassagem, Integer permanenciaMinutos, Integer vagaPretendida) {
        this.aeroviaId = aeroviaId;
        this.altitude = altitude;
        this.entradaPrevista = entradaPrevista;
        this.chegadaPrevista = chegadaPrevista;
        this.tipoPassagem = tipoPassagem;
        this.permanenciaMinutos = permanenciaMinutos;
        this.vagaPretendida = vagaPretendida;
    }

    public String getAeroviaId() { return aeroviaId; }
    public Integer getAltitude() { return altitude; }
    public LocalDateTime getEntradaPrevista() { return entradaPrevista; }
    public LocalDateTime getChegadaPrevista() { return chegadaPrevista; }
    public String getTipoPassagem() { return tipoPassagem; }
    public Integer getPermanenciaMinutos() { return permanenciaMinutos; }
    public Integer getVagaPretendida() { return vagaPretendida; }
}
