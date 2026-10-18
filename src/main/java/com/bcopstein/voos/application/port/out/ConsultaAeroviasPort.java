package com.bcopstein.voos.application.port.out;

import com.bcopstein.voos.domain.model.rede.Aerovia;
import java.util.List;
import java.util.Optional;

public interface ConsultaAeroviasPort {
    List<Aerovia> listar();

    Optional<Aerovia> buscar(String id);

    Aerovia salvar(Aerovia aerovia);
}
