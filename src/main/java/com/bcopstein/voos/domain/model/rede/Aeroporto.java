package com.bcopstein.voos.domain.model.rede;

import com.bcopstein.voos.domain.exception.DadosInvalidosException;

public class Aeroporto {
    private final String codigo;
    private final String nome;
    private final int vagas;

    public Aeroporto(String codigo, String nome, int vagas) {
        if (codigo == null || codigo.isBlank()) {
            throw new DadosInvalidosException("Aeroporto deve ter código.");
        }
        if (vagas < 0) {
            throw new DadosInvalidosException("Quantidade de vagas não pode ser negativa.");
        }
        this.codigo = codigo;
        this.nome = nome;
        this.vagas = vagas;
    }

    /** As vagas são numeradas de 1 até a quantidade cadastrada. */
    public boolean possuiVaga(int numero) {
        return numero >= 1 && numero <= vagas;
    }

    public String getCodigo() { return codigo; }

    public String getNome() { return nome; }

    public int getVagas() { return vagas; }
}
