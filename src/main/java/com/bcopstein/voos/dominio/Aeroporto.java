package com.bcopstein.voos.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Aeroporto {
    @Id
    private String codigo; // Ex: POA, FLN, CWB, GRU
    private String nome;
    private int vagas;

    public Aeroporto() {}

    public Aeroporto(String codigo, String nome, int vagas) {
        this.codigo = codigo;
        this.nome = nome;
        this.vagas = vagas;
    }

    public String getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public int getVagas() { return vagas; }
}
