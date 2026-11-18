package com.bcopstein.registro;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "aprovacoes")
public class AprovacaoEntity {
    @Id
    @Column(name = "evento_id", length = 36)
    private String eventoId;
    @Column(name = "plano_id", nullable = false, unique = true)
    private Long planoId;
    @Column(name = "recebido_em", nullable = false)
    private LocalDateTime recebidoEm;
    @Lob
    @Column(name = "payload", nullable = false)
    private String payload;

    protected AprovacaoEntity() {}
    public AprovacaoEntity(String eventoId, Long planoId, LocalDateTime recebidoEm, String payload) {
        this.eventoId = eventoId;
        this.planoId = planoId;
        this.recebidoEm = recebidoEm;
        this.payload = payload;
    }
    public String getEventoId() { return eventoId; }
    public Long getPlanoId() { return planoId; }
    public LocalDateTime getRecebidoEm() { return recebidoEm; }
    public String getPayload() { return payload; }
}
