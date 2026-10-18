package com.bcopstein.voos.application.port.out;

import com.bcopstein.voos.domain.model.rede.Aeroporto;
import java.util.List;
import java.util.Optional;

public interface ConsultaAeroportosPort {
    List<Aeroporto> listar();

    Optional<Aeroporto> buscar(String codigo);

    Aeroporto salvar(Aeroporto aeroporto);
}
