package com.bcopstein.voos.application.port.out;

import com.bcopstein.voos.domain.model.piloto.Piloto;
import java.util.List;
import java.util.Optional;

public interface ConsultaPilotosPort {
    List<Piloto> listar();

    Optional<Piloto> buscar(Long id);
}
