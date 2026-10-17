package com.bcopstein.voos.domain.model.piloto;

public class Piloto {
    private final Long id;
    private final String nome;

    public Piloto(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Long getId() { return id; }

    public String getNome() { return nome; }
}
