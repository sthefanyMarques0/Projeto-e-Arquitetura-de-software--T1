package com.bcopstein.voos.dominio;

import java.time.LocalDateTime;
import java.util.List;

public class Periodo {
    private final LocalDateTime inicio;
    private final LocalDateTime fim;

    public Periodo(LocalDateTime inicio, LocalDateTime fim) {
        this.inicio = inicio;
        this.fim = fim;
    }

    public LocalDateTime getInicio() { return inicio; }
    public LocalDateTime getFim() { return fim; }

    public List<Slot> getSlots() {
        return Slot.calcularSlots(inicio, fim);
    }
}
