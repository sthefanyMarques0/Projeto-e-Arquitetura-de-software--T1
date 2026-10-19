package com.bcopstein.voos.adapter.in.rest.mapper;

import com.bcopstein.voos.adapter.in.rest.dto.AeronaveRequest;
import com.bcopstein.voos.adapter.in.rest.dto.AeronaveResponse;
import com.bcopstein.voos.application.port.in.DadosAeronaveCommand;
import com.bcopstein.voos.domain.model.aeronave.Aeronave;
import com.bcopstein.voos.domain.model.aeronave.AeronaveCarga;
import com.bcopstein.voos.domain.model.aeronave.AeronaveParticular;
import com.bcopstein.voos.domain.model.aeronave.AeronavePassageiros;

public final class AeronaveRestMapper {
    private AeronaveRestMapper() {}

    public static DadosAeronaveCommand toCommand(AeronaveRequest r) {
        return new DadosAeronaveCommand(r.prefixo(), r.categoria(), r.velocidadeCruzeiroKmH(), r.autonomiaNominalKm(),
                r.empresaManutencao(), r.companhiaAerea(), r.capacidadeMaximaPassageiros(), r.capacidadeMaximaToneladas());
    }

    public static AeronaveResponse toResponse(Aeronave aeronave) {
        if (aeronave instanceof AeronaveParticular a) {
            return new AeronaveResponse(a.getPrefixo(), a.getCategoria().name(),
                    a.getVelocidadeCruzeiroKmH(), a.getAutonomiaNominalKm(),
                    a.getEmpresaManutencao(), null, null, null);
        }
        if (aeronave instanceof AeronavePassageiros a) {
            return new AeronaveResponse(a.getPrefixo(), a.getCategoria().name(),
                    a.getVelocidadeCruzeiroKmH(), a.getAutonomiaNominalKm(),
                    null, a.getCompanhiaAerea(), a.getCapacidadeMaximaPassageiros(), null);
        }
        if (aeronave instanceof AeronaveCarga a) {
            return new AeronaveResponse(a.getPrefixo(), a.getCategoria().name(),
                    a.getVelocidadeCruzeiroKmH(), a.getAutonomiaNominalKm(),
                    null, a.getCompanhiaAerea(), null, a.getCapacidadeMaximaToneladas());
        }
        throw new IllegalArgumentException("Tipo de aeronave não suportado");
    }
}
