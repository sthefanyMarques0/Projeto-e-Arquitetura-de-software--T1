package com.bcopstein.voos.dominio;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Slot {
    private final LocalDateTime inicio;
    private final LocalDateTime fim;

    public Slot(LocalDateTime inicio, LocalDateTime fim) {
        this.inicio = inicio;
        this.fim = fim;
    }

    public LocalDateTime getInicio() { return inicio; }
    public LocalDateTime getFim() { return fim; }

    public static List<Slot> calcularSlots(LocalDateTime de, LocalDateTime ate) {
        List<Slot> slots = new ArrayList<>();
        LocalDateTime inicioHora = de.truncatedTo(ChronoUnit.HOURS);
        LocalDateTime cursor = inicioHora;
        while (cursor.isBefore(ate)) {
            LocalDateTime proximo = cursor.plusHours(1);
            slots.add(new Slot(cursor, proximo));
            cursor = proximo;
        }
        return slots;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Slot slot)) return false;
        return Objects.equals(inicio, slot.inicio) && Objects.equals(fim, slot.fim);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inicio, fim);
    }

    @Override
    public String toString() {
        return inicio.toLocalTime() + "-" + fim.toLocalTime();
    }
}
