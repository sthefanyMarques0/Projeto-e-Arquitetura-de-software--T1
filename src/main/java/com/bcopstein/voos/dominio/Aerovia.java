package com.bcopstein.voos.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.Duration;

@Entity
public class Aerovia {
    @Id
    private String id; // Ex: R1, R2, etc.

    @ManyToOne(optional = false)
    private Aeroporto origem;

    @ManyToOne(optional = false)
    private Aeroporto destino;

    private double distanciaKm;

    public Aerovia() {}

    public Aerovia(String id, Aeroporto origem, Aeroporto destino, double distanciaKm) {
        this.id = id;
        this.origem = origem;
        this.destino = destino;
        this.distanciaKm = distanciaKm;
    }

    public Duration getDuracao(Aeronave aeronave) {
        double horas = this.distanciaKm / aeronave.getVelocidadeCruzeiro();
        long minutos = Math.round(horas * 60);
        return Duration.ofMinutes(minutos);
    }

    public String getId() { return id; }
    public Aeroporto getOrigem() { return origem; }
    public Aeroporto getDestino() { return destino; }
    public double getDistanciaKm() { return distanciaKm; }
}
