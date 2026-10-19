package com.bcopstein.voos.application.port.in;

import com.bcopstein.voos.domain.model.piloto.Piloto;

public interface CadastrarPiloto {
    Piloto cadastrar(Long id, String nome);
}
