package com.bcopstein.voos.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bcopstein.voos.domain.model.tempo.Periodo;
import com.bcopstein.voos.domain.model.tempo.Slot;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class PeriodoTest {
    private static LocalDateTime h(int hora, int minuto) {
        return LocalDateTime.of(2027, 1, 15, hora, minuto);
    }

    @Test
    void exemploDoEnunciadoAerovia() {
        // entrada 15:52 e saída 17:07 ocupa 15–16, 16–17 e 17–18
        List<Slot> slots = new Periodo(h(15, 52), h(17, 7)).slots();
        assertEquals(List.of(new Slot(h(15, 0)), new Slot(h(16, 0)), new Slot(h(17, 0))), slots);
    }

    @Test
    void exemploDoEnunciadoVaga() {
        // chegada 15:50 com 20 minutos de permanência ocupa 15–16 e 16–17
        assertEquals(2, new Periodo(h(15, 50), h(16, 10)).slots().size());
    }

    @Test
    void terminarNaHoraCheiaNaoOcupaOProximoSlot() {
        assertEquals(List.of(new Slot(h(15, 0))), new Periodo(h(15, 0), h(16, 0)).slots());
    }
}
