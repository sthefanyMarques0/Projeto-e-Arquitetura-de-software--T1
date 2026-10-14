package com.bcopstein.voos.domain.model.reserva;

import com.bcopstein.voos.domain.model.tempo.Slot;
import java.util.Objects;

/**
 * Ocupação de um recurso (altitude de uma aerovia ou vaga de um aeroporto) durante um slot.
 * Um mesmo recurso não pode ter duas reservas no mesmo slot.
 */
public class Reserva {
    private final Long id;
    private final Long planoId;
    private final TipoReserva tipo;
    private final String aeroviaId;
    private final Integer altitude;
    private final String aeroportoCodigo;
    private final Integer vaga;
    private final Slot slot;

    public Reserva(Long id, Long planoId, TipoReserva tipo, String aeroviaId, Integer altitude,
                   String aeroportoCodigo, Integer vaga, Slot slot) {
        this.id = id;
        this.planoId = planoId;
        this.tipo = Objects.requireNonNull(tipo);
        this.aeroviaId = aeroviaId;
        this.altitude = altitude;
        this.aeroportoCodigo = aeroportoCodigo;
        this.vaga = vaga;
        this.slot = Objects.requireNonNull(slot);
    }

    public static Reserva deAltitude(Long planoId, String aeroviaId, int altitude, Slot slot) {
        return new Reserva(null, planoId, TipoReserva.ALTITUDE_AEROVIA, aeroviaId, altitude, null, null, slot);
    }

    public static Reserva deVaga(Long planoId, String aeroportoCodigo, int vaga, Slot slot) {
        return new Reserva(null, planoId, TipoReserva.VAGA_AEROPORTO, null, null, aeroportoCodigo, vaga, slot);
    }

    /** Identifica o recurso ocupado (sem o slot), ex.: "aerovia R1 a 29000 pés". */
    public String recurso() {
        return tipo == TipoReserva.ALTITUDE_AEROVIA
                ? "aerovia " + aeroviaId + " a " + altitude + " pés"
                : "vaga " + vaga + " de " + aeroportoCodigo;
    }

    /** Duas reservas conflitam quando ocupam o mesmo recurso no mesmo slot. */
    public boolean conflitaCom(Reserva outra) {
        return tipo == outra.tipo && slot.equals(outra.slot)
                && Objects.equals(aeroviaId, outra.aeroviaId) && Objects.equals(altitude, outra.altitude)
                && Objects.equals(aeroportoCodigo, outra.aeroportoCodigo) && Objects.equals(vaga, outra.vaga);
    }

    public Long getId() { return id; }

    public Long getPlanoId() { return planoId; }

    public TipoReserva getTipo() { return tipo; }

    public String getAeroviaId() { return aeroviaId; }

    public Integer getAltitude() { return altitude; }

    public String getAeroportoCodigo() { return aeroportoCodigo; }

    public Integer getVaga() { return vaga; }

    public Slot getSlot() { return slot; }
}
