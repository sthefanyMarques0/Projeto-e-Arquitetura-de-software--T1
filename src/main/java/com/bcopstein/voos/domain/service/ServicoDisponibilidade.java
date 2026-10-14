package com.bcopstein.voos.domain.service;

import com.bcopstein.voos.domain.model.aeronave.Aeronave;
import com.bcopstein.voos.domain.model.rede.Aeroporto;
import com.bcopstein.voos.domain.model.rede.Aerovia;
import com.bcopstein.voos.domain.model.reserva.Reserva;
import com.bcopstein.voos.domain.model.reserva.TipoReserva;
import com.bcopstein.voos.domain.model.tempo.Periodo;
import com.bcopstein.voos.domain.model.tempo.Slot;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Verifica a ocupação dos recursos a partir das reservas existentes, recebidas como parâmetro.
 * Não acessa o banco: quem chama (o caso de uso) carrega as reservas pela porta de saída.
 */
public class ServicoDisponibilidade {

    /** Reservas pretendidas que conflitam com as existentes. */
    public List<Reserva> conflitos(Collection<Reserva> pretendidas, Collection<Reserva> existentes) {
        return pretendidas.stream()
                .filter(p -> existentes.stream().anyMatch(p::conflitaCom))
                .toList();
    }

    public boolean altitudeLivre(String aeroviaId, int altitude, Periodo periodo, Collection<Reserva> existentes) {
        return periodo.slots().stream().noneMatch(slot ->
                existentes.stream().anyMatch(r -> r.conflitaCom(Reserva.deAltitude(null, aeroviaId, altitude, slot))));
    }

    public boolean vagaLivre(String aeroportoCodigo, int vaga, Periodo periodo, Collection<Reserva> existentes) {
        return periodo.slots().stream().noneMatch(slot ->
                existentes.stream().anyMatch(r -> r.conflitaCom(Reserva.deVaga(null, aeroportoCodigo, vaga, slot))));
    }

    /**
     * Altitudes livres em todos os slots do período. Se a aeronave for informada,
     * considera também as regras de operação da sua categoria.
     */
    public List<Integer> altitudesLivres(Aerovia aerovia, Periodo periodo, Aeronave aeronaveOpcional,
                                         Collection<Reserva> existentes) {
        List<Reserva> daAerovia = existentes.stream()
                .filter(r -> r.getTipo() == TipoReserva.ALTITUDE_AEROVIA && aerovia.getId().equals(r.getAeroviaId()))
                .toList();
        List<Integer> livres = new ArrayList<>();
        for (int altitude : Aerovia.altitudesPrevistas()) {
            boolean permitida = aeronaveOpcional == null || aeronaveOpcional.permiteOperar(altitude, periodo);
            if (permitida && altitudeLivre(aerovia.getId(), altitude, periodo, daAerovia)) {
                livres.add(altitude);
            }
        }
        return livres;
    }

    /** Números das vagas livres em todos os slots do período. */
    public List<Integer> vagasLivres(Aeroporto aeroporto, Periodo periodo, Collection<Reserva> existentes) {
        List<Slot> slots = periodo.slots();
        List<Reserva> doAeroporto = existentes.stream()
                .filter(r -> r.getTipo() == TipoReserva.VAGA_AEROPORTO && aeroporto.getCodigo().equals(r.getAeroportoCodigo()))
                .filter(r -> slots.contains(r.getSlot()))
                .toList();
        List<Integer> livres = new ArrayList<>();
        for (int vaga = 1; vaga <= aeroporto.getVagas(); vaga++) {
            final int numero = vaga;
            if (doAeroporto.stream().noneMatch(r -> r.getVaga() == numero)) {
                livres.add(numero);
            }
        }
        return livres;
    }
}
