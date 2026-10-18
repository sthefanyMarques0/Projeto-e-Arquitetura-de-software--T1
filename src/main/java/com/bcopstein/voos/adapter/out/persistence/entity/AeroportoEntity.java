package com.bcopstein.voos.adapter.out.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "aeroportos")
public class AeroportoEntity {
    @Id
    private String codigo;
    private String nome;
    private Integer vagas;

    protected AeroportoEntity() {}

    public AeroportoEntity(String codigo, String nome, Integer vagas) {
        this.codigo = codigo;
        this.nome = nome;
        this.vagas = vagas;
    }

    public String getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public Integer getVagas() { return vagas; }
}
