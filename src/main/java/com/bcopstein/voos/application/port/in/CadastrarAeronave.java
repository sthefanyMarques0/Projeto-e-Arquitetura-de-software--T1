package com.bcopstein.voos.application.port.in;

import com.bcopstein.voos.domain.model.aeronave.Aeronave;

public interface CadastrarAeronave {
    Aeronave cadastrar(DadosAeronaveCommand dados);
}
