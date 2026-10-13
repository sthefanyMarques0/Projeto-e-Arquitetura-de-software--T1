package com.bcopstein.voos.dominio;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class PlanoDeVoo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private SituacaoPlano situacao = SituacaoPlano.EM_EDICAO;

    private LocalDateTime partidaPrevista;

    @ManyToOne(optional = false)
    private Piloto piloto;

    @ManyToOne(optional = false)
    private Aeronave aeronave;

    @ManyToOne(optional = false)
    private Aeroporto origem;

    @ManyToOne(optional = false)
    private Aeroporto destino;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "ordem_trecho")
    private List<Trecho> trechos = new ArrayList<>();

    public PlanoDeVoo() {}

    public PlanoDeVoo(LocalDateTime partidaPrevista, Piloto piloto, Aeronave aeronave, Aeroporto origem, Aeroporto destino) {
        this.partidaPrevista = partidaPrevista;
        this.piloto = piloto;
        this.aeronave = aeronave;
        this.origem = origem;
        this.destino = destino;
        this.situacao = SituacaoPlano.EM_EDICAO;
    }

    public void adicionarTrecho(Trecho t) {
        this.trechos.add(t);
    }

    public void removerTrecho(Trecho t) {
        this.trechos.remove(t);
    }

    public LocalDateTime getChegadaFinal() {
        if (trechos.isEmpty()) return partidaPrevista;
        return trechos.get(trechos.size() - 1).getChegadaPrevista();
    }

    public Long getId() { return id; }
    public SituacaoPlano getSituacao() { return situacao; }
    public void setSituacao(SituacaoPlano situacao) { this.situacao = situacao; }
    public LocalDateTime getPartidaPrevista() { return partidaPrevista; }
    public void setPartidaPrevista(LocalDateTime partidaPrevista) { this.partidaPrevista = partidaPrevista; }
    public Piloto getPiloto() { return piloto; }
    public void setPiloto(Piloto piloto) { this.piloto = piloto; }
    public Aeronave getAeronave() { return aeronave; }
    public void setAeronave(Aeronave aeronave) { this.aeronave = aeronave; }
    public Aeroporto getOrigem() { return origem; }
    public void setOrigem(Aeroporto origem) { this.origem = origem; }
    public Aeroporto getDestino() { return destino; }
    public void setDestino(Aeroporto destino) { this.destino = destino; }
    public List<Trecho> getTrechos() { return trechos; }
}
