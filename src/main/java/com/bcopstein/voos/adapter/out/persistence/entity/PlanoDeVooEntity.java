package com.bcopstein.voos.adapter.out.persistence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "planos_de_voo")
public class PlanoDeVooEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "piloto_id", nullable = false)
    private Long pilotoId;
    @Column(name = "aeronave_prefixo", nullable = false)
    private String aeronavePrefixo;
    @Column(name = "origem_codigo", nullable = false)
    private String origemCodigo;
    @Column(name = "destino_codigo", nullable = false)
    private String destinoCodigo;
    @Column(name = "partida_prevista", nullable = false)
    private LocalDateTime partidaPrevista;
    @Column(name = "situacao", nullable = false)
    private String situacao;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "trechos", joinColumns = @JoinColumn(name = "plano_id"))
    @OrderColumn(name = "ordem")
    private List<TrechoEmbeddable> trechos = new ArrayList<>();

    protected PlanoDeVooEntity() {}

    public PlanoDeVooEntity(Long id) {
        this.id = id;
    }

    public void atualizar(Long pilotoId, String aeronavePrefixo, String origemCodigo, String destinoCodigo,
                          LocalDateTime partidaPrevista, String situacao, List<TrechoEmbeddable> novosTrechos) {
        this.pilotoId = pilotoId;
        this.aeronavePrefixo = aeronavePrefixo;
        this.origemCodigo = origemCodigo;
        this.destinoCodigo = destinoCodigo;
        this.partidaPrevista = partidaPrevista;
        this.situacao = situacao;
        this.trechos.clear();
        this.trechos.addAll(novosTrechos);
    }

    public Long getId() { return id; }
    public Long getPilotoId() { return pilotoId; }
    public String getAeronavePrefixo() { return aeronavePrefixo; }
    public String getOrigemCodigo() { return origemCodigo; }
    public String getDestinoCodigo() { return destinoCodigo; }
    public LocalDateTime getPartidaPrevista() { return partidaPrevista; }
    public String getSituacao() { return situacao; }
    public List<TrechoEmbeddable> getTrechos() { return trechos; }
}
