package com.bcopstein.voos.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Piloto {
    @Id
    private Long id;
    private String nome;

    public Piloto() {}

    public Piloto(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
}
