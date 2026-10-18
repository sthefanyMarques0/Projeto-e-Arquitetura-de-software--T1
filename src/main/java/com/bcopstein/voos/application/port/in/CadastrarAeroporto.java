package com.bcopstein.voos.application.port.in;

import com.bcopstein.voos.domain.model.rede.Aeroporto;

public interface CadastrarAeroporto {
    Aeroporto cadastrar(String codigo, String nome, int vagas);
}
