package com.bcopstein.voos.domain.model.plano;

import com.bcopstein.voos.domain.exception.DadosInvalidosException;
import com.bcopstein.voos.domain.model.aeronave.Aeronave;
import com.bcopstein.voos.domain.model.rede.Aerovia;
import com.bcopstein.voos.domain.model.tempo.Periodo;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Trecho do percurso: uma aerovia percorrida numa altitude, com o que ocorre ao chegar
 * ao aeroporto de destino da aerovia. A chegada é sempre calculada pelo sistema.
 */
public class Trecho {
    private final Aerovia aerovia;
    private final int altitude;
    private final LocalDateTime entradaPrevista;
    private final LocalDateTime chegadaPrevista;
    private final TipoPassagem tipoPassagem;
    private final Integer permanenciaMinutos;
    private final Integer vagaPretendida;

    private Trecho(Aerovia aerovia, int altitude, LocalDateTime entradaPrevista, LocalDateTime chegadaPrevista,
                   TipoPassagem tipoPassagem, Integer permanenciaMinutos, Integer vagaPretendida) {
        this.aerovia = aerovia;
        this.altitude = altitude;
        this.entradaPrevista = entradaPrevista;
        this.chegadaPrevista = chegadaPrevista;
        this.tipoPassagem = tipoPassagem;
        this.permanenciaMinutos = permanenciaMinutos;
        this.vagaPretendida = vagaPretendida;
    }

    /** Cria um trecho calculando a chegada: entrada + ceil(distância / velocidade). */
    public static Trecho criar(Aerovia aerovia, int altitude, LocalDateTime entradaPrevista,
                               TipoPassagem tipoPassagem, Integer permanenciaMinutos, Integer vagaPretendida,
                               Aeronave aeronave) {
        if (aerovia == null || entradaPrevista == null || tipoPassagem == null || aeronave == null) {
            throw new DadosInvalidosException("Trecho deve informar aerovia, entrada prevista e tipo de passagem.");
        }
        if (tipoPassagem == TipoPassagem.POUSO) {
            if (permanenciaMinutos == null || permanenciaMinutos < 1) {
                throw new DadosInvalidosException("Pouso em " + aerovia.getDestino().getCodigo()
                        + " exige permanência inteira de pelo menos 1 minuto.");
            }
            if (vagaPretendida == null) {
                throw new DadosInvalidosException("Pouso em " + aerovia.getDestino().getCodigo()
                        + " exige a vaga pretendida.");
            }
        } else if (permanenciaMinutos != null || vagaPretendida != null) {
            throw new DadosInvalidosException("Sobrevoo de " + aerovia.getDestino().getCodigo()
                    + " não utiliza vaga nem permanência.");
        }
        LocalDateTime chegada = entradaPrevista.plus(aerovia.duracaoPara(aeronave));
        return new Trecho(aerovia, altitude, entradaPrevista, chegada, tipoPassagem, permanenciaMinutos, vagaPretendida);
    }

    /** Reconstrói um trecho já persistido, mantendo a chegada registrada. */
    public static Trecho reconstituir(Aerovia aerovia, int altitude, LocalDateTime entradaPrevista,
                                      LocalDateTime chegadaPrevista, TipoPassagem tipoPassagem,
                                      Integer permanenciaMinutos, Integer vagaPretendida) {
        return new Trecho(aerovia, altitude, entradaPrevista, chegadaPrevista, tipoPassagem, permanenciaMinutos, vagaPretendida);
    }

    /** Recalcula a chegada para outra aeronave (quando a aeronave do plano é trocada). */
    public Trecho recalcularPara(Aeronave aeronave) {
        return criar(aerovia, altitude, entradaPrevista, tipoPassagem, permanenciaMinutos, vagaPretendida, aeronave);
    }

    public boolean isPouso() {
        return tipoPassagem == TipoPassagem.POUSO;
    }

    /** Período de utilização da aerovia: [entrada, chegada). */
    public Periodo periodoNaAerovia() {
        return new Periodo(entradaPrevista, chegadaPrevista);
    }

    /** Período em solo no aeroporto de destino, quando houver pouso: [chegada, chegada + permanência). */
    public Optional<Periodo> periodoEmSolo() {
        if (!isPouso()) {
            return Optional.empty();
        }
        return Optional.of(new Periodo(chegadaPrevista, chegadaPrevista.plusMinutes(permanenciaMinutos)));
    }

    /** Momento em que a aeronave pode entrar na próxima aerovia. */
    public LocalDateTime liberacaoParaProximoTrecho() {
        return isPouso() ? chegadaPrevista.plusMinutes(permanenciaMinutos) : chegadaPrevista;
    }

    public Aerovia getAerovia() { return aerovia; }

    public int getAltitude() { return altitude; }

    public LocalDateTime getEntradaPrevista() { return entradaPrevista; }

    public LocalDateTime getChegadaPrevista() { return chegadaPrevista; }

    public TipoPassagem getTipoPassagem() { return tipoPassagem; }

    public Integer getPermanenciaMinutos() { return permanenciaMinutos; }

    public Integer getVagaPretendida() { return vagaPretendida; }
}
