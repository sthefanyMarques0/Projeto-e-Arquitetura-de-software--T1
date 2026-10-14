package com.bcopstein.voos.adapter.in.rest.mapper;

import com.bcopstein.voos.adapter.in.rest.dto.AeroportoResponse;
import com.bcopstein.voos.adapter.in.rest.dto.AeroviaResponse;
import com.bcopstein.voos.adapter.in.rest.dto.PilotoResponse;
import com.bcopstein.voos.domain.model.piloto.Piloto;
import com.bcopstein.voos.domain.model.rede.Aeroporto;
import com.bcopstein.voos.domain.model.rede.Aerovia;

public final class RedeAereaRestMapper {
    private RedeAereaRestMapper() {}

    public static AeroportoResponse toResponse(Aeroporto a) {
        return new AeroportoResponse(a.getCodigo(), a.getNome(), a.getVagas());
    }

    public static AeroviaResponse toResponse(Aerovia a) {
        return new AeroviaResponse(a.getId(), a.getOrigem().getCodigo(), a.getDestino().getCodigo(), a.getDistanciaKm());
    }

    public static PilotoResponse toResponse(Piloto p) {
        return new PilotoResponse(p.getId(), p.getNome());
    }
}
