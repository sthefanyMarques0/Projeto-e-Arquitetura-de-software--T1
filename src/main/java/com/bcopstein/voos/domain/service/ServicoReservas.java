package com.bcopstein.voos.domain.service;

import com.bcopstein.voos.domain.model.plano.PlanoDeVoo;
import com.bcopstein.voos.domain.model.plano.Trecho;
import com.bcopstein.voos.domain.model.reserva.Reserva;
import com.bcopstein.voos.domain.model.tempo.Slot;
import java.util.ArrayList;
import java.util.List;

/** Determina as reservas que um plano precisa: uma por slot de altitude e uma por slot de vaga. */
public class ServicoReservas {

    public List<Reserva> reservasNecessarias(PlanoDeVoo plano) {
        List<Reserva> reservas = new ArrayList<>();
        for (Trecho trecho : plano.getTrechos()) {
            reservas.addAll(reservasDoTrecho(plano.getId(), trecho));
        }
        return reservas;
    }

    public List<Reserva> reservasDoTrecho(Long planoId, Trecho trecho) {
        List<Reserva> reservas = new ArrayList<>(reservasDeAltitude(planoId, trecho));
        reservas.addAll(reservasDeVaga(planoId, trecho));
        return reservas;
    }

    public List<Reserva> reservasDeAltitude(Long planoId, Trecho trecho) {
        List<Reserva> reservas = new ArrayList<>();
        for (Slot slot : trecho.periodoNaAerovia().slots()) {
            reservas.add(Reserva.deAltitude(planoId, trecho.getAerovia().getId(), trecho.getAltitude(), slot));
        }
        return reservas;
    }

    public List<Reserva> reservasDeVaga(Long planoId, Trecho trecho) {
        List<Reserva> reservas = new ArrayList<>();
        trecho.periodoEmSolo().ifPresent(periodo -> {
            for (Slot slot : periodo.slots()) {
                reservas.add(Reserva.deVaga(planoId, trecho.getAerovia().getDestino().getCodigo(),
                        trecho.getVagaPretendida(), slot));
            }
        });
        return reservas;
    }
}
