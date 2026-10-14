package com.bcopstein.voos.domain.model.tempo;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/** Intervalo de uma hora alinhado à hora cheia: [inicio, inicio + 1h). */
public record Slot(LocalDateTime inicio) {

    public Slot {
        Objects.requireNonNull(inicio, "inicio do slot");
        if (!inicio.equals(inicio.truncatedTo(ChronoUnit.HOURS))) {
            throw new IllegalArgumentException("Slot deve iniciar na hora cheia: " + inicio);
        }
    }

    public static Slot contendo(LocalDateTime instante) {
        return new Slot(instante.truncatedTo(ChronoUnit.HOURS));
    }

    public LocalDateTime fim() {
        return inicio.plusHours(1);
    }

    @Override
    public String toString() {
        return inicio.toLocalDate() + " " + inicio.toLocalTime() + "–" + fim().toLocalTime();
    }
}
