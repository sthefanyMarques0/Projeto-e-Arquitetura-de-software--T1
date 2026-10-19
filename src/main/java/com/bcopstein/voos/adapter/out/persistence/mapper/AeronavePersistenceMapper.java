package com.bcopstein.voos.adapter.out.persistence.mapper;

import com.bcopstein.voos.adapter.out.persistence.entity.AeronaveEntity;
import com.bcopstein.voos.domain.model.aeronave.Aeronave;
import com.bcopstein.voos.domain.model.aeronave.AeronaveCarga;
import com.bcopstein.voos.domain.model.aeronave.AeronaveParticular;
import com.bcopstein.voos.domain.model.aeronave.AeronavePassageiros;

public final class AeronavePersistenceMapper {
    private AeronavePersistenceMapper() {}

    public static Aeronave toDomain(AeronaveEntity entity) {
        return switch (entity.getCategoria()) {
            case "PARTICULAR" -> new AeronaveParticular(entity.getPrefixo(), entity.getVelocidadeCruzeiroKmH(), entity.getAutonomiaNominalKm(), entity.getEmpresaManutencao());
            case "COMERCIAL_PASSAGEIROS" -> new AeronavePassageiros(entity.getPrefixo(), entity.getVelocidadeCruzeiroKmH(), entity.getAutonomiaNominalKm(), entity.getCompanhiaAerea(), entity.getCapacidadeMaximaPassageiros());
            case "COMERCIAL_CARGA" -> new AeronaveCarga(entity.getPrefixo(), entity.getVelocidadeCruzeiroKmH(), entity.getAutonomiaNominalKm(), entity.getCompanhiaAerea(), entity.getCapacidadeMaximaToneladas());
            default -> throw new IllegalStateException("Categoria persistida desconhecida: " + entity.getCategoria());
        };
    }

    public static AeronaveEntity toEntity(Aeronave aeronave) {
        String categoria = aeronave.getCategoria().name();
        if (aeronave instanceof AeronaveParticular a) {
            return new AeronaveEntity(a.getPrefixo(), categoria, a.getVelocidadeCruzeiroKmH(), a.getAutonomiaNominalKm(),
                    a.getEmpresaManutencao(), null, null, null);
        }
        if (aeronave instanceof AeronavePassageiros a) {
            return new AeronaveEntity(a.getPrefixo(), categoria, a.getVelocidadeCruzeiroKmH(), a.getAutonomiaNominalKm(),
                    null, a.getCompanhiaAerea(), a.getCapacidadeMaximaPassageiros(), null);
        }
        if (aeronave instanceof AeronaveCarga a) {
            return new AeronaveEntity(a.getPrefixo(), categoria, a.getVelocidadeCruzeiroKmH(), a.getAutonomiaNominalKm(),
                    null, a.getCompanhiaAerea(), null, a.getCapacidadeMaximaToneladas());
        }
        throw new IllegalArgumentException("Tipo de aeronave não suportado: " + aeronave.getClass().getSimpleName());
    }
}
