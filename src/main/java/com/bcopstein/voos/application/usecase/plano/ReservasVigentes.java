package com.bcopstein.voos.application.usecase.plano;

import com.bcopstein.voos.application.port.out.ReservaPort;
import com.bcopstein.voos.domain.model.plano.PlanoDeVoo;
import com.bcopstein.voos.domain.model.plano.Trecho;
import com.bcopstein.voos.domain.model.reserva.Reserva;
import com.bcopstein.voos.domain.model.tempo.Periodo;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

/** Carrega as reservas existentes que podem conflitar com o plano (do início do 1º trecho à saída do último pouso). */
@Component
public class ReservasVigentes {
    private final ReservaPort reservas;

    public ReservasVigentes(ReservaPort reservas) {
        this.reservas = reservas;
    }

    public List<Reserva> paraPlano(PlanoDeVoo plano) {
        List<Trecho> trechos = plano.getTrechos();
        if (trechos.isEmpty()) {
            return List.of();
        }
        LocalDateTime inicio = trechos.stream().map(Trecho::getEntradaPrevista).min(Comparator.naturalOrder()).orElseThrow();
        LocalDateTime fim = trechos.stream().map(Trecho::liberacaoParaProximoTrecho).max(Comparator.naturalOrder()).orElseThrow();
        if (!inicio.isBefore(fim)) {
            fim = inicio.plusHours(1);
        }
        return reservas.buscarNoPeriodo(new Periodo(inicio, fim));
    }
}
