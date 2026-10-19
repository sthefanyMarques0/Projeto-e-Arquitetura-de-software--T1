package com.bcopstein.voos.application.port.out;

import com.bcopstein.voos.domain.model.aeronave.Aeronave;
import java.util.List;
import java.util.Optional;

public interface ConsultaAeronavesPort {
    List<Aeronave> listar();

    Optional<Aeronave> buscar(String prefixo);

    Aeronave salvar(Aeronave aeronave);
}
