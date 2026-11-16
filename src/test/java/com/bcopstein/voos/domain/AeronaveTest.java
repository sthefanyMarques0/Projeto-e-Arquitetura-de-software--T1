package com.bcopstein.voos.domain;

import static com.bcopstein.voos.domain.Fixtures.CARGA;
import static com.bcopstein.voos.domain.Fixtures.PARTICULAR;
import static com.bcopstein.voos.domain.Fixtures.PASSAGEIROS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bcopstein.voos.domain.model.tempo.Periodo;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class AeronaveTest {
    private static final LocalDateTime DIA = LocalDateTime.of(2027, 1, 15, 0, 0);

    @Test
    void duracaoArredondaSegundosParaCima() {
        // 363 km / 850 km/h = 1537,41 s -> 1538 s
        assertEquals(Duration.ofSeconds(1538), PASSAGEIROS.tempoDeVoo(new BigDecimal("363")));
        // 865 km / 750 km/h = 4152 s exatos
        assertEquals(Duration.ofSeconds(4152), CARGA.tempoDeVoo(new BigDecimal("865")));
    }

    @Test
    void autonomiaSeguraEh85PorCento() {
        assertEquals(0, new BigDecimal("3570").compareTo(PASSAGEIROS.getAutonomiaSeguraKm()));
    }

    @Test
    void particularSomente25a27MilPes() {
        Periodo p = new Periodo(DIA.withHour(10), DIA.withHour(11));
        assertTrue(PARTICULAR.permiteOperar(26_000, p));
        assertFalse(PARTICULAR.permiteOperar(28_000, p));
    }

    @Test
    void passageirosA28MilPesOuMais() {
        Periodo p = new Periodo(DIA.withHour(10), DIA.withHour(11));
        assertTrue(PASSAGEIROS.permiteOperar(28_000, p));
        assertFalse(PASSAGEIROS.permiteOperar(27_000, p));
    }

    @Test
    void cargaSomenteEntreZeroESeisHoras() {
        assertTrue(CARGA.permiteOperar(30_000, new Periodo(DIA.withHour(2), DIA.withHour(3))));
        // pode terminar exatamente às 06:00
        assertTrue(CARGA.permiteOperar(30_000, new Periodo(DIA.withHour(5), DIA.withHour(6))));
        // não pode iniciar às 06:00 nem ultrapassar
        assertFalse(CARGA.permiteOperar(30_000, new Periodo(DIA.withHour(6), DIA.withHour(7))));
        assertFalse(CARGA.permiteOperar(30_000, new Periodo(DIA.withHour(5), DIA.withHour(6).plusMinutes(1))));
    }
}
