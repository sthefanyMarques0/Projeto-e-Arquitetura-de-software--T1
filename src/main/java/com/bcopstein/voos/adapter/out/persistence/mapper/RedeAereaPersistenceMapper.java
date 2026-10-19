package com.bcopstein.voos.adapter.out.persistence.mapper;

import com.bcopstein.voos.adapter.out.persistence.entity.AeroportoEntity;
import com.bcopstein.voos.adapter.out.persistence.entity.AeroviaEntity;
import com.bcopstein.voos.adapter.out.persistence.entity.PilotoEntity;
import com.bcopstein.voos.domain.model.piloto.Piloto;
import com.bcopstein.voos.domain.model.rede.Aeroporto;
import com.bcopstein.voos.domain.model.rede.Aerovia;
import java.util.Map;

public final class RedeAereaPersistenceMapper {
    private RedeAereaPersistenceMapper() {}

    public static Aeroporto toDomain(AeroportoEntity entity) {
        return new Aeroporto(entity.getCodigo(), entity.getNome(), entity.getVagas());
    }

    public static Aerovia toDomain(AeroviaEntity entity, Map<String, Aeroporto> aeroportosPorCodigo) {
        return new Aerovia(entity.getId(),
                exigir(aeroportosPorCodigo, entity.getOrigemCodigo()),
                exigir(aeroportosPorCodigo, entity.getDestinoCodigo()),
                entity.getDistanciaKm());
    }

    public static AeroportoEntity toEntity(Aeroporto aeroporto) {
        return new AeroportoEntity(aeroporto.getCodigo(), aeroporto.getNome(), aeroporto.getVagas());
    }

    public static AeroviaEntity toEntity(Aerovia aerovia) {
        return new AeroviaEntity(aerovia.getId(), aerovia.getOrigem().getCodigo(),
                aerovia.getDestino().getCodigo(), aerovia.getDistanciaKm());
    }

    public static Piloto toDomain(PilotoEntity entity) {
        return new Piloto(entity.getId(), entity.getNome());
    }

    public static PilotoEntity toEntity(Piloto piloto) {
        return new PilotoEntity(piloto.getId(), piloto.getNome());
    }

    private static Aeroporto exigir(Map<String, Aeroporto> aeroportos, String codigo) {
        Aeroporto aeroporto = aeroportos.get(codigo);
        if (aeroporto == null) {
            throw new IllegalStateException("Aerovia referencia aeroporto inexistente: " + codigo);
        }
        return aeroporto;
    }
}
