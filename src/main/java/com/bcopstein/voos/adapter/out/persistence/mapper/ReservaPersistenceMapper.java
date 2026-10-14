package com.bcopstein.voos.adapter.out.persistence.mapper;

import com.bcopstein.voos.adapter.out.persistence.entity.ReservaEntity;
import com.bcopstein.voos.domain.model.reserva.Reserva;
import com.bcopstein.voos.domain.model.reserva.TipoReserva;
import com.bcopstein.voos.domain.model.tempo.Slot;

public final class ReservaPersistenceMapper {
    private ReservaPersistenceMapper() {}

    public static Reserva toDomain(ReservaEntity e) {
        return new Reserva(e.getId(), e.getPlanoId(), TipoReserva.valueOf(e.getTipo()), e.getAeroviaId(), e.getAltitude(),
                e.getAeroportoCodigo(), e.getVaga(), new Slot(e.getInicioSlot()));
    }

    public static ReservaEntity toEntity(Reserva r) {
        return new ReservaEntity(r.getPlanoId(), r.getTipo().name(), r.getAeroviaId(), r.getAltitude(),
                r.getAeroportoCodigo(), r.getVaga(), r.getSlot().inicio());
    }
}
