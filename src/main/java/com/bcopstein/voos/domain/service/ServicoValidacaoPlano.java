package com.bcopstein.voos.domain.service;

import com.bcopstein.voos.domain.model.aeronave.Aeronave;
import com.bcopstein.voos.domain.model.plano.PlanoDeVoo;
import com.bcopstein.voos.domain.model.plano.ResultadoValidacao;
import com.bcopstein.voos.domain.model.plano.Trecho;
import com.bcopstein.voos.domain.model.rede.Aeroporto;
import com.bcopstein.voos.domain.model.rede.Aerovia;
import com.bcopstein.voos.domain.model.reserva.Reserva;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Aplica todas as regras da seção 6 do enunciado sem alterar o plano nem reservar recursos.
 * As reservas existentes são recebidas como parâmetro (situação no momento da validação).
 */
public class ServicoValidacaoPlano {
    private final ServicoAutonomia servicoAutonomia;
    private final ServicoDisponibilidade servicoDisponibilidade;
    private final ServicoReservas servicoReservas;

    public ServicoValidacaoPlano(ServicoAutonomia servicoAutonomia, ServicoDisponibilidade servicoDisponibilidade,
                                 ServicoReservas servicoReservas) {
        this.servicoAutonomia = servicoAutonomia;
        this.servicoDisponibilidade = servicoDisponibilidade;
        this.servicoReservas = servicoReservas;
    }

    public ResultadoValidacao validar(PlanoDeVoo plano, Collection<Reserva> reservasExistentes) {
        ResultadoValidacao resultado = new ResultadoValidacao();
        List<Trecho> trechos = plano.getTrechos();
        if (trechos.isEmpty()) {
            resultado.adicionarErro("O plano deve conter um ou mais trechos.");
            return resultado;
        }
        validarPercurso(plano, trechos, resultado);
        validarHorarios(plano, trechos, resultado);
        validarAeronave(plano.getAeronave(), trechos, resultado);
        resultado.adicionarErros(servicoAutonomia.verificar(plano));
        validarDisponibilidade(plano, trechos, reservasExistentes, resultado);
        return resultado;
    }

    private void validarPercurso(PlanoDeVoo plano, List<Trecho> trechos, ResultadoValidacao resultado) {
        Trecho primeiro = trechos.get(0);
        Trecho ultimo = trechos.get(trechos.size() - 1);
        if (!mesmoAeroporto(primeiro.getAerovia().getOrigem(), plano.getOrigem())) {
            resultado.adicionarErro("A origem da primeira aerovia (" + primeiro.getAerovia().getId() + ": "
                    + primeiro.getAerovia().getOrigem().getCodigo() + ") difere da origem do plano ("
                    + plano.getOrigem().getCodigo() + ").");
        }
        for (int i = 0; i < trechos.size() - 1; i++) {
            Aerovia atual = trechos.get(i).getAerovia();
            Aerovia proxima = trechos.get(i + 1).getAerovia();
            if (!mesmoAeroporto(atual.getDestino(), proxima.getOrigem())) {
                resultado.adicionarErro(String.format("Percurso descontínuo entre os trechos %d e %d: %s termina em %s, mas %s começa em %s.",
                        i + 1, i + 2, atual.getId(), atual.getDestino().getCodigo(), proxima.getId(), proxima.getOrigem().getCodigo()));
            }
        }
        if (!mesmoAeroporto(ultimo.getAerovia().getDestino(), plano.getDestino())) {
            resultado.adicionarErro("O destino da última aerovia (" + ultimo.getAerovia().getId() + ": "
                    + ultimo.getAerovia().getDestino().getCodigo() + ") difere do destino do plano ("
                    + plano.getDestino().getCodigo() + ").");
        }
        if (!ultimo.isPouso()) {
            resultado.adicionarErro("No destino final (" + ultimo.getAerovia().getDestino().getCodigo()
                    + ") deve ocorrer obrigatoriamente um pouso.");
        }
    }

    private void validarHorarios(PlanoDeVoo plano, List<Trecho> trechos, ResultadoValidacao resultado) {
        Trecho primeiro = trechos.get(0);
        if (!primeiro.getEntradaPrevista().isEqual(plano.getPartidaPrevista())) {
            resultado.adicionarErro("A entrada na primeira aerovia (" + primeiro.getEntradaPrevista()
                    + ") deve ser igual à partida do plano (" + plano.getPartidaPrevista() + ").");
        }
        for (int i = 0; i < trechos.size(); i++) {
            Trecho trecho = trechos.get(i);
            LocalDateTime chegadaCalculada = trecho.getEntradaPrevista().plus(trecho.getAerovia().duracaoPara(plano.getAeronave()));
            if (!trecho.getChegadaPrevista().isEqual(chegadaCalculada)) {
                resultado.adicionarErro(String.format("Trecho %d (%s): chegada registrada (%s) incompatível com a duração calculada (chegada esperada %s).",
                        i + 1, trecho.getAerovia().getId(), trecho.getChegadaPrevista(), chegadaCalculada));
            }
            if (i < trechos.size() - 1) {
                Trecho proximo = trechos.get(i + 1);
                LocalDateTime entradaEsperada = trecho.liberacaoParaProximoTrecho();
                if (!proximo.getEntradaPrevista().isEqual(entradaEsperada)) {
                    String regra = trecho.isPouso()
                            ? "após pouso, deve ser a chegada + " + trecho.getPermanenciaMinutos() + " min de permanência"
                            : "após sobrevoo, deve ser igual à chegada";
                    resultado.adicionarErro(String.format("Trecho %d (%s): entrada prevista %s incompatível com a duração calculada; %s (%s).",
                            i + 2, proximo.getAerovia().getId(), proximo.getEntradaPrevista(), regra, entradaEsperada));
                }
            }
        }
        LocalDateTime chegadaFinal = trechos.get(trechos.size() - 1).getChegadaPrevista();
        if (!chegadaFinal.toLocalDate().isEqual(plano.getPartidaPrevista().toLocalDate())) {
            resultado.adicionarErro("O voo deve iniciar e terminar no mesmo dia: partida em "
                    + plano.getPartidaPrevista().toLocalDate() + " e chegada ao destino final em " + chegadaFinal + ".");
        }
    }

    private void validarAeronave(Aeronave aeronave, List<Trecho> trechos, ResultadoValidacao resultado) {
        for (int i = 0; i < trechos.size(); i++) {
            Trecho trecho = trechos.get(i);
            if (!Aerovia.altitudeNaFaixa(trecho.getAltitude())) {
                resultado.adicionarErro(String.format("Trecho %d (%s): altitude %d fora da faixa permitida (25.000 a 35.000 pés, de 1.000 em 1.000).",
                        i + 1, trecho.getAerovia().getId(), trecho.getAltitude()));
            } else if (!aeronave.permiteOperar(trecho.getAltitude(), trecho.periodoNaAerovia())) {
                resultado.adicionarErro(String.format("Trecho %d (%s): %s não pode operar a %d pés entre %s e %s (%s).",
                        i + 1, trecho.getAerovia().getId(), aeronave.getPrefixo(), trecho.getAltitude(),
                        trecho.getEntradaPrevista(), trecho.getChegadaPrevista(), aeronave.descricaoRestricao()));
            }
        }
    }

    private void validarDisponibilidade(PlanoDeVoo plano, List<Trecho> trechos, Collection<Reserva> reservasExistentes,
                                        ResultadoValidacao resultado) {
        List<Reserva> deOutrosPlanos = reservasExistentes.stream()
                .filter(r -> plano.getId() == null || !Objects.equals(r.getPlanoId(), plano.getId()))
                .toList();
        List<Reserva> jaPretendidas = new ArrayList<>();
        for (int i = 0; i < trechos.size(); i++) {
            Trecho trecho = trechos.get(i);
            List<Reserva> altitudes = servicoReservas.reservasDeAltitude(plano.getId(), trecho);
            registrarConflitos(i, trecho, altitudes, deOutrosPlanos, jaPretendidas, resultado);
            jaPretendidas.addAll(altitudes);

            if (trecho.isPouso()) {
                Aeroporto aeroporto = trecho.getAerovia().getDestino();
                if (!aeroporto.possuiVaga(trecho.getVagaPretendida())) {
                    resultado.adicionarErro(String.format("Trecho %d (%s): a vaga %d não existe em %s (vagas de 1 a %d).",
                            i + 1, trecho.getAerovia().getId(), trecho.getVagaPretendida(), aeroporto.getCodigo(), aeroporto.getVagas()));
                } else {
                    List<Reserva> vagas = servicoReservas.reservasDeVaga(plano.getId(), trecho);
                    registrarConflitos(i, trecho, vagas, deOutrosPlanos, jaPretendidas, resultado);
                    jaPretendidas.addAll(vagas);
                }
            }
        }
    }

    private void registrarConflitos(int indice, Trecho trecho, List<Reserva> pretendidas, List<Reserva> existentes,
                                    List<Reserva> jaPretendidas, ResultadoValidacao resultado) {
        List<Reserva> comOutros = servicoDisponibilidade.conflitos(pretendidas, existentes);
        if (!comOutros.isEmpty()) {
            resultado.adicionarErro(String.format("Trecho %d (%s): %s indisponível nos slots %s (reservado por outro plano aprovado).",
                    indice + 1, trecho.getAerovia().getId(), comOutros.get(0).recurso(), slots(comOutros)));
        }
        List<Reserva> internos = servicoDisponibilidade.conflitos(pretendidas, jaPretendidas);
        if (!internos.isEmpty()) {
            resultado.adicionarErro(String.format("Trecho %d (%s): %s já é usada por outro trecho do próprio plano nos slots %s.",
                    indice + 1, trecho.getAerovia().getId(), internos.get(0).recurso(), slots(internos)));
        }
    }

    private static String slots(List<Reserva> reservas) {
        return reservas.stream().map(r -> r.getSlot().toString()).distinct().toList().toString();
    }

    private static boolean mesmoAeroporto(Aeroporto a, Aeroporto b) {
        return a.getCodigo().equalsIgnoreCase(b.getCodigo());
    }
}
