package com.bcopstein.voos.adapter.out.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pilotos")
public class PilotoEntity {
    @Id
    private Long id;
    private String nome;

    protected PilotoEntity() {}

    public PilotoEntity(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
}
