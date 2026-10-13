package com.bcopstein.voos.servico;

import com.bcopstein.voos.dominio.*;
import com.bcopstein.voos.repositorio.ReservaRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServicoDisponibilidade {
    private final ReservaRepository reservaRepository;

    public ServicoDisponibilidade(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public boolean verificarAltitude(Aerovia aerovia, int altitude, Periodo periodo) {
        for (Slot slot : periodo.getSlots()) {
            List<Reserva> conflitos = reservaRepository
                .findByAeroviaAndAltitudeAndSlotInicioLessThanAndSlotFimGreaterThan(
                    aerovia, altitude, slot.getFim(), slot.getInicio());
            if (!conflitos.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean verificarVaga(Aeroporto aeroporto, int vaga, Periodo periodo) {
        if (vaga <= 0 || vaga > aeroporto.getVagas()) {
            return false;
        }
        for (Slot slot : periodo.getSlots()) {
            List<Reserva> conflitos = reservaRepository
                .findByAeroportoAndVagaAndSlotInicioLessThanAndSlotFimGreaterThan(
                    aeroporto, vaga, slot.getFim(), slot.getInicio());
            if (!conflitos.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public List<Integer> altitudesDisponiveis(Aerovia aerovia, Periodo periodo, Aeronave aeronave) {
        List<Integer> disponiveis = new ArrayList<>();
        for (int alt = 25000; alt <= 35000; alt += 1000) {
            if (aeronave.validarRegrasOperacao(alt, periodo.getInicio(), periodo.getFim())) {
                if (verificarAltitude(aerovia, alt, periodo)) {
                    disponiveis.add(alt);
                }
            }
        }
        return disponiveis;
    }

    public int vagasDisponiveis(Aeroporto aeroporto, Periodo periodo) {
        int maxOcupadas = 0;
        for (Slot slot : periodo.getSlots()) {
            List<Reserva> reservas = reservaRepository
                .findByAeroportoAndSlotInicioLessThanAndSlotFimGreaterThan(
                    aeroporto, slot.getFim(), slot.getInicio());
            if (reservas.size() > maxOcupadas) {
                maxOcupadas = reservas.size();
            }
        }
        return Math.max(0, aeroporto.getVagas() - maxOcupadas);
    }
}
