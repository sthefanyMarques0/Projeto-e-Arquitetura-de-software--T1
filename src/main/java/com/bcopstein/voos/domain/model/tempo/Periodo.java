package com.bcopstein.voos.domain.model.tempo;

import com.bcopstein.voos.domain.exception.DadosInvalidosException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Período de utilização de um recurso, no formato [inicio, fim): o início é incluído
 * e o fim é excluído. Qualquer utilização parcial de uma hora ocupa o slot inteiro.
 */
public record Periodo(LocalDateTime inicio, LocalDateTime fim) {

    public Periodo {
        if (inicio == null || fim == null) {
            throw new DadosInvalidosException("Período deve ter início e fim.");
        }
        if (!inicio.isBefore(fim)) {
            throw new DadosInvalidosException("O início do período deve ser anterior ao fim.");
        }
    }

    /** Slots de uma hora necessários para cobrir o período. Terminar na hora cheia não ocupa o slot seguinte. */
    public List<Slot> slots() {
        List<Slot> slots = new ArrayList<>();
        Slot atual = Slot.contendo(inicio);
        while (atual.inicio().isBefore(fim)) {
            slots.add(atual);
            atual = new Slot(atual.fim());
        }
        return slots;
    }
}
