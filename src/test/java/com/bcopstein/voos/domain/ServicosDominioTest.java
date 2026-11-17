package com.bcopstein.voos.domain;

import static com.bcopstein.voos.domain.Fixtures.POA;
import static com.bcopstein.voos.domain.Fixtures.POUCA_AUTONOMIA;
import static com.bcopstein.voos.domain.Fixtures.R1;
import static com.bcopstein.voos.domain.Fixtures.R4;
import static com.bcopstein.voos.domain.Fixtures.REF_08H;
import static com.bcopstein.voos.domain.Fixtures.plano;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bcopstein.voos.domain.model.plano.TipoPassagem;
import com.bcopstein.voos.domain.model.plano.Trecho;
import com.bcopstein.voos.domain.model.reserva.Reserva;
import com.bcopstein.voos.domain.model.tempo.Periodo;
import com.bcopstein.voos.domain.model.tempo.Slot;
import com.bcopstein.voos.domain.service.ServicoAutonomia;
import com.bcopstein.voos.domain.service.ServicoDisponibilidade;
import java.util.List;
import org.junit.jupiter.api.Test;

class ServicosDominioTest {
    private final ServicoAutonomia autonomia = new ServicoAutonomia();
    private final ServicoDisponibilidade disponibilidade = new ServicoDisponibilidade();

    @Test
    void autonomiaAcumulaSobrevoosAteExcederLimiteSeguro() {
        Trecho primeiro = Trecho.criar(R1, 25_000, REF_08H, TipoPassagem.SOBREVOO, null, null, POUCA_AUTONOMIA);
        Trecho segundo = Trecho.criar(R4, 25_000, primeiro.getChegadaPrevista(),
                TipoPassagem.POUSO, 30, 1, POUCA_AUTONOMIA);

        List<String> erros = autonomia.verificar(plano(
                POUCA_AUTONOMIA, POA, R4.getDestino(), REF_08H, List.of(primeiro, segundo)));

        assertEquals(1, erros.size());
        assertTrue(erros.get(0).contains("608 km"));
    }

    @Test
    void pousoIntermediarioZeraDistanciaAcumulada() {
        Trecho primeiro = Trecho.criar(R1, 25_000, REF_08H, TipoPassagem.POUSO, 30, 1, POUCA_AUTONOMIA);
        Trecho segundo = Trecho.criar(R4, 25_000, primeiro.liberacaoParaProximoTrecho(),
                TipoPassagem.POUSO, 30, 1, POUCA_AUTONOMIA);

        assertTrue(autonomia.verificar(plano(
                POUCA_AUTONOMIA, POA, R4.getDestino(), REF_08H, List.of(primeiro, segundo))).isEmpty());
    }

    @Test
    void disponibilidadeIdentificaConflitosDeAltitudeEVagaNoMesmoSlot() {
        Slot slot = new Slot(REF_08H);
        Periodo periodo = new Periodo(REF_08H, REF_08H.plusMinutes(30));
        List<Reserva> existentes = List.of(
                Reserva.deAltitude(1L, "R1", 29_000, slot),
                Reserva.deVaga(1L, "FLN", 1, slot));

        assertFalse(disponibilidade.altitudeLivre("R1", 29_000, periodo, existentes));
        assertTrue(disponibilidade.altitudeLivre("R1", 30_000, periodo, existentes));
        assertFalse(disponibilidade.vagaLivre("FLN", 1, periodo, existentes));
        assertTrue(disponibilidade.vagaLivre("FLN", 2, periodo, existentes));
        assertEquals(0, disponibilidade.conflitos(
                List.of(Reserva.deAltitude(2L, "R1", 30_000, slot)), existentes).size());
        assertEquals(1, disponibilidade.conflitos(
                List.of(Reserva.deAltitude(2L, "R1", 29_000, slot)), existentes).size());
    }
}
