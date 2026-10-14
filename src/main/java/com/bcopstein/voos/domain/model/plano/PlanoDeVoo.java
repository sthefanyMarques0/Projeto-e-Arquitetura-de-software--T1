package com.bcopstein.voos.domain.model.plano;

import com.bcopstein.voos.domain.exception.DadosInvalidosException;
import com.bcopstein.voos.domain.exception.OperacaoNaoPermitidaException;
import com.bcopstein.voos.domain.model.aeronave.Aeronave;
import com.bcopstein.voos.domain.model.piloto.Piloto;
import com.bcopstein.voos.domain.model.rede.Aeroporto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Raiz do agregado: o plano e seus trechos são alterados somente por meio dele,
 * o que garante que somente planos em edição sejam modificados.
 */
public class PlanoDeVoo {
    private final Long id;
    private Piloto piloto;
    private Aeronave aeronave;
    private Aeroporto origem;
    private Aeroporto destino;
    private LocalDateTime partidaPrevista;
    private SituacaoPlano situacao;
    private List<Trecho> trechos;

    private PlanoDeVoo(Long id, Piloto piloto, Aeronave aeronave, Aeroporto origem, Aeroporto destino,
                       LocalDateTime partidaPrevista, SituacaoPlano situacao, List<Trecho> trechos) {
        this.id = id;
        this.piloto = piloto;
        this.aeronave = aeronave;
        this.origem = origem;
        this.destino = destino;
        this.partidaPrevista = partidaPrevista;
        this.situacao = situacao;
        this.trechos = new ArrayList<>(trechos);
    }

    /** Todo plano nasce em edição e sem percurso. */
    public static PlanoDeVoo criar(Piloto piloto, Aeronave aeronave, Aeroporto origem, Aeroporto destino,
                                   LocalDateTime partidaPrevista) {
        exigirDados(piloto, aeronave, origem, destino, partidaPrevista);
        return new PlanoDeVoo(null, piloto, aeronave, origem, destino, partidaPrevista, SituacaoPlano.EM_EDICAO, List.of());
    }

    /** Reconstrói um plano já persistido. */
    public static PlanoDeVoo reconstituir(Long id, Piloto piloto, Aeronave aeronave, Aeroporto origem, Aeroporto destino,
                                          LocalDateTime partidaPrevista, SituacaoPlano situacao, List<Trecho> trechos) {
        return new PlanoDeVoo(id, piloto, aeronave, origem, destino, partidaPrevista, situacao, trechos);
    }

    /** Altera os dados gerais. Se a aeronave mudar, as chegadas dos trechos são recalculadas. */
    public void alterarDados(Piloto piloto, Aeronave aeronave, Aeroporto origem, Aeroporto destino,
                             LocalDateTime partidaPrevista) {
        garantirEmEdicao("alterado");
        exigirDados(piloto, aeronave, origem, destino, partidaPrevista);
        this.piloto = piloto;
        this.aeronave = aeronave;
        this.origem = origem;
        this.destino = destino;
        this.partidaPrevista = partidaPrevista;
        this.trechos = new ArrayList<>(trechos.stream().map(t -> t.recalcularPara(aeronave)).toList());
    }

    /** Substitui o percurso inteiro (adicionar, remover ou reordenar trechos). */
    public void definirPercurso(List<Trecho> novosTrechos) {
        garantirEmEdicao("alterado");
        this.trechos = new ArrayList<>(novosTrechos);
    }

    public void aprovar() {
        garantirEmEdicao("aprovado");
        this.situacao = SituacaoPlano.APROVADO;
    }

    public void abandonar() {
        garantirEmEdicao("abandonado");
        this.situacao = SituacaoPlano.ABANDONADO;
    }

    /** Somente plano aprovado cuja partida ainda não ocorreu pode ser cancelado. */
    public void cancelar(LocalDateTime agora) {
        if (situacao != SituacaoPlano.APROVADO) {
            throw new OperacaoNaoPermitidaException("Somente planos aprovados podem ser cancelados. Situação atual: " + situacao + ".");
        }
        if (!partidaPrevista.isAfter(agora)) {
            throw new OperacaoNaoPermitidaException("A partida prevista (" + partidaPrevista
                    + ") já ocorreu: o voo é considerado executado e não pode ser cancelado.");
        }
        this.situacao = SituacaoPlano.CANCELADO;
    }

    public void garantirEmEdicao(String operacao) {
        if (situacao != SituacaoPlano.EM_EDICAO) {
            throw new OperacaoNaoPermitidaException("Somente planos em edição podem ser " + operacao
                    + ". Situação atual: " + situacao + ".");
        }
    }

    public boolean partidaEhPosteriorA(LocalDateTime instante) {
        return partidaPrevista.isAfter(instante);
    }

    public Optional<LocalDateTime> getChegadaFinal() {
        return trechos.isEmpty() ? Optional.empty() : Optional.of(trechos.get(trechos.size() - 1).getChegadaPrevista());
    }

    private static void exigirDados(Piloto piloto, Aeronave aeronave, Aeroporto origem, Aeroporto destino,
                                    LocalDateTime partidaPrevista) {
        if (piloto == null || aeronave == null || origem == null || destino == null || partidaPrevista == null) {
            throw new DadosInvalidosException("Plano deve informar piloto, aeronave, origem, destino e partida prevista.");
        }
    }

    public Long getId() { return id; }

    public Piloto getPiloto() { return piloto; }

    public Aeronave getAeronave() { return aeronave; }

    public Aeroporto getOrigem() { return origem; }

    public Aeroporto getDestino() { return destino; }

    public LocalDateTime getPartidaPrevista() { return partidaPrevista; }

    public SituacaoPlano getSituacao() { return situacao; }

    public List<Trecho> getTrechos() { return List.copyOf(trechos); }
}
