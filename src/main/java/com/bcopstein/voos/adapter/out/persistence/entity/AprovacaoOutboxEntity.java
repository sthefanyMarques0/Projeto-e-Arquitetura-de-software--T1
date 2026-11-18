package com.bcopstein.voos.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_aprovacoes")
public class AprovacaoOutboxEntity {
    @Id
    @Column(name = "evento_id", length = 36)
    private String eventoId;
    @Column(name = "plano_id", nullable = false)
    private Long planoId;
    @Lob
    @Column(name = "payload", nullable = false)
    private String payload;
    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;
    @Column(name = "publicado_em")
    private LocalDateTime publicadoEm;
    @Column(name = "tentativas", nullable = false)
    private int tentativas;

    protected AprovacaoOutboxEntity() {}

    public AprovacaoOutboxEntity(String eventoId, Long planoId, String payload, LocalDateTime criadoEm) {
        this.eventoId = eventoId;
        this.planoId = planoId;
        this.payload = payload;
        this.criadoEm = criadoEm;
    }

    public String getEventoId() { return eventoId; }
    public Long getPlanoId() { return planoId; }
    public String getPayload() { return payload; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getPublicadoEm() { return publicadoEm; }
    public int getTentativas() { return tentativas; }
    public void registrarTentativa() { tentativas++; }
    public void marcarPublicado(LocalDateTime quando) { publicadoEm = quando; }
}
