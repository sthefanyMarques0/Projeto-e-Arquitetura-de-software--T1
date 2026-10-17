package com.bcopstein.voos.domain;

import static com.bcopstein.voos.domain.Fixtures.CWB;
import static com.bcopstein.voos.domain.Fixtures.FLN;
import static com.bcopstein.voos.domain.Fixtures.GRU;
import static com.bcopstein.voos.domain.Fixtures.PARTICULAR;
import static com.bcopstein.voos.domain.Fixtures.PASSAGEIROS;
import static com.bcopstein.voos.domain.Fixtures.POA;
import static com.bcopstein.voos.domain.Fixtures.POUCA_AUTONOMIA;
import static com.bcopstein.voos.domain.Fixtures.R1;
import static com.bcopstein.voos.domain.Fixtures.R4;
import static com.bcopstein.voos.domain.Fixtures.R6;
import static com.bcopstein.voos.domain.Fixtures.REF_08H;
import static com.bcopstein.voos.domain.Fixtures.plano;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bcopstein.voos.domain.exception.DadosInvalidosException;
import com.bcopstein.voos.domain.exception.OperacaoNaoPermitidaException;
import com.bcopstein.voos.domain.model.plano.PlanoDeVoo;
import com.bcopstein.voos.domain.model.plano.ResultadoValidacao;
import com.bcopstein.voos.domain.model.plano.TipoPassagem;
import com.bcopstein.voos.domain.model.plano.Trecho;
import com.bcopstein.voos.domain.model.reserva.Reserva;
import com.bcopstein.voos.domain.model.tempo.Slot;
import com.bcopstein.voos.domain.service.ServicoAutonomia;
import com.bcopstein.voos.domain.service.ServicoDisponibilidade;
import com.bcopstein.voos.domain.service.ServicoReservas;
import com.bcopstein.voos.domain.service.ServicoValidacaoPlano;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class ServicoValidacaoPlanoTest {
    private final ServicoReservas reservas = new ServicoReservas();
    private final ServicoValidacaoPlano validacao =
            new ServicoValidacaoPlano(new ServicoAutonomia(), new ServicoDisponibilidade(), reservas);

    private static boolean contem(ResultadoValidacao r, String trecho) {
        return r.getErros().stream().anyMatch(e -> e.contains(trecho));
    }

    /** POA -R1-> FLN (sobrevoo) -R4-> CWB (pouso), como o plano 1 da carga inicial. */
    private PlanoDeVoo poaFlnCwb(LocalDateTime partida, int altitude) {
        Trecho t1 = Trecho.criar(R1, altitude, partida, TipoPassagem.SOBREVOO, null, null, PASSAGEIROS);
        Trecho t2 = Trecho.criar(R4, altitude, t1.getChegadaPrevista(), TipoPassagem.POUSO, 60, 1, PASSAGEIROS);
        return plano(PASSAGEIROS, POA, CWB, partida, List.of(t1, t2));
    }

    @Test
    void planoCorretoEhValido() {
        ResultadoValidacao r = validacao.validar(poaFlnCwb(REF_08H, 29_000), List.of());
        assertTrue(r.isValido(), r.getErros().toString());
    }

    @Test
    void chegadaCalculadaPeloSistema() {
        PlanoDeVoo p = poaFlnCwb(REF_08H, 29_000);
        assertEquals(LocalDateTime.of(2026, 11, 20, 8, 25, 38), p.getTrechos().get(0).getChegadaPrevista());
        assertEquals(LocalDateTime.of(2026, 11, 20, 8, 42, 56), p.getTrechos().get(1).getChegadaPrevista());
    }

    @Test
    void semTrechosEhInvalido() {
        ResultadoValidacao r = validacao.validar(plano(PASSAGEIROS, POA, CWB, REF_08H, List.of()), List.of());
        assertFalse(r.isValido());
    }

    @Test
    void percursoDescontinuo() {
        Trecho t1 = Trecho.criar(R1, 29_000, REF_08H, TipoPassagem.SOBREVOO, null, null, PASSAGEIROS);
        Trecho t2 = Trecho.criar(R6, 29_000, t1.getChegadaPrevista(), TipoPassagem.POUSO, 30, 1, PASSAGEIROS);
        ResultadoValidacao r = validacao.validar(plano(PASSAGEIROS, POA, GRU, REF_08H, List.of(t1, t2)), List.of());
        assertTrue(contem(r, "descontínuo"), r.getErros().toString());
    }

    @Test
    void entradaIncompativelComDuracaoCalculada() {
        Trecho t1 = Trecho.criar(R1, 29_000, REF_08H, TipoPassagem.SOBREVOO, null, null, PASSAGEIROS);
        Trecho t2 = Trecho.criar(R4, 29_000, REF_08H.plusMinutes(20), TipoPassagem.POUSO, 60, 1, PASSAGEIROS);
        ResultadoValidacao r = validacao.validar(plano(PASSAGEIROS, POA, CWB, REF_08H, List.of(t1, t2)), List.of());
        assertTrue(contem(r, "incompatível com a duração calculada"), r.getErros().toString());
    }

    @Test
    void destinoFinalExigePouso() {
        Trecho t1 = Trecho.criar(R1, 29_000, REF_08H, TipoPassagem.SOBREVOO, null, null, PASSAGEIROS);
        ResultadoValidacao r = validacao.validar(plano(PASSAGEIROS, POA, FLN, REF_08H, List.of(t1)), List.of());
        assertTrue(contem(r, "pouso"), r.getErros().toString());
    }

    @Test
    void restricaoDaCategoria() {
        Trecho t1 = Trecho.criar(R1, 29_000, REF_08H, TipoPassagem.POUSO, 30, 1, PARTICULAR);
        ResultadoValidacao r = validacao.validar(plano(PARTICULAR, POA, FLN, REF_08H, List.of(t1)), List.of());
        assertTrue(contem(r, "particular"), r.getErros().toString());
    }

    @Test
    void autonomiaInsuficienteComSobrevoo() {
        // PT-ECO: 85% de 500 km = 425 km; R1 (363) + R4 (245) sem pouso = 608 km
        Trecho t1 = Trecho.criar(R1, 25_000, REF_08H, TipoPassagem.SOBREVOO, null, null, POUCA_AUTONOMIA);
        Trecho t2 = Trecho.criar(R4, 25_000, t1.getChegadaPrevista(), TipoPassagem.POUSO, 30, 1, POUCA_AUTONOMIA);
        ResultadoValidacao r = validacao.validar(plano(POUCA_AUTONOMIA, POA, CWB, REF_08H, List.of(t1, t2)), List.of());
        assertTrue(contem(r, "autonomia"), r.getErros().toString());
    }

    @Test
    void pousoIntermediarioReabasteceAeronave() {
        Trecho t1 = Trecho.criar(R1, 25_000, REF_08H, TipoPassagem.POUSO, 30, 1, POUCA_AUTONOMIA);
        Trecho t2 = Trecho.criar(R4, 25_000, t1.liberacaoParaProximoTrecho(), TipoPassagem.POUSO, 30, 1, POUCA_AUTONOMIA);
        ResultadoValidacao r = validacao.validar(plano(POUCA_AUTONOMIA, POA, CWB, REF_08H, List.of(t1, t2)), List.of());
        assertTrue(r.isValido(), r.getErros().toString());
    }

    @Test
    void conflitoDeAltitudeComPlanoAprovado() {
        List<Reserva> existentes = List.of(Reserva.deAltitude(99L, "R1", 29_000, new Slot(REF_08H)));
        ResultadoValidacao r = validacao.validar(poaFlnCwb(REF_08H.plusMinutes(10), 29_000), existentes);
        assertTrue(contem(r, "aerovia R1 a 29000 pés indisponível"), r.getErros().toString());
    }

    @Test
    void vagaOcupadaOuInexistente() {
        List<Reserva> existentes = List.of(Reserva.deVaga(99L, "CWB", 1, new Slot(REF_08H.plusHours(1))));
        ResultadoValidacao r = validacao.validar(poaFlnCwb(REF_08H, 29_000), existentes);
        assertTrue(contem(r, "vaga 1 de CWB indisponível"), r.getErros().toString());

        Trecho t1 = Trecho.criar(R1, 29_000, REF_08H, TipoPassagem.POUSO, 30, 99, PASSAGEIROS);
        ResultadoValidacao r2 = validacao.validar(plano(PASSAGEIROS, POA, FLN, REF_08H, List.of(t1)), List.of());
        assertTrue(contem(r2, "não existe"), r2.getErros().toString());
    }

    @Test
    void vooDeveTerminarNoMesmoDia() {
        LocalDateTime tarde = LocalDateTime.of(2026, 11, 20, 23, 50);
        Trecho t1 = Trecho.criar(R1, 29_000, tarde, TipoPassagem.POUSO, 30, 1, PASSAGEIROS);
        ResultadoValidacao r = validacao.validar(plano(PASSAGEIROS, POA, FLN, tarde, List.of(t1)), List.of());
        assertTrue(contem(r, "mesmo dia"), r.getErros().toString());
    }

    @Test
    void reservasNecessariasPorSlot() {
        // R1 08:00–08:25 e R4 08:25–08:42 (slot 08) + vaga CWB 08:42–09:42 (slots 08 e 09)
        assertEquals(4, reservas.reservasNecessarias(poaFlnCwb(REF_08H, 29_000)).size());
    }

    @Test
    void pousoExigePermanenciaMinimaDeUmMinuto() {
        assertThrows(DadosInvalidosException.class,
                () -> Trecho.criar(R1, 29_000, REF_08H, TipoPassagem.POUSO, 0, 1, PASSAGEIROS));
    }

    @Test
    void somentePlanoEmEdicaoPodeSerAlterado() {
        PlanoDeVoo p = poaFlnCwb(REF_08H, 29_000);
        p.aprovar();
        assertThrows(OperacaoNaoPermitidaException.class, () -> p.definirPercurso(List.of()));
        assertThrows(OperacaoNaoPermitidaException.class, p::abandonar);
    }

    @Test
    void cancelamentoSomenteAntesDaPartida() {
        PlanoDeVoo futuro = poaFlnCwb(REF_08H, 29_000);
        futuro.aprovar();
        assertThrows(OperacaoNaoPermitidaException.class, () -> futuro.cancelar(REF_08H));
        futuro.cancelar(REF_08H.minusMinutes(1));
        assertEquals("CANCELADO", futuro.getSituacao().name());
    }
}
