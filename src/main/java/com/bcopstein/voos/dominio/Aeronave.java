package com.bcopstein.voos.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
public class Aeronave {
    @Id
    private String prefixo;
    private double velocidadeCruzeiro; // km/h
    private double autonomiaKm;

    @Enumerated(EnumType.STRING)
    private CategoriaAeronave categoria;

    private String empresaManutencao;
    private String companhiaAerea;
    private int capacidadePassageiros;
    private double capacidadeCargaTon;

    public Aeronave() {}

    public Aeronave(String prefixo, double velocidadeCruzeiro, double autonomiaKm, CategoriaAeronave categoria) {
        this.prefixo = prefixo;
        this.velocidadeCruzeiro = velocidadeCruzeiro;
        this.autonomiaKm = autonomiaKm;
        this.categoria = categoria;
    }

    // Regra da secao 3 do PAS_TF: margem de seguranca de 15% (distancia sem reabastecimento <= 85%)
    public double getAutonomiaSegura() {
        return this.autonomiaKm * 0.85;
    }

    // Validacao das regras de altitude e horario por categoria
    public boolean validarRegrasOperacao(int altitude, LocalDateTime entrada, LocalDateTime saida) {
        if (altitude < 25000 || altitude > 35000) {
            return false;
        }

        switch (this.categoria) {
            case PARTICULAR:
                return altitude == 25000 || altitude == 26000 || altitude == 27000;
            case COMERCIAL_PASSAGEIROS:
                return altitude >= 28000;
            case COMERCIAL_CARGA:
                LocalTime limiteInicio = LocalTime.of(0, 0);
                LocalTime limiteFim = LocalTime.of(6, 0);
                LocalTime horaEntrada = entrada.toLocalTime();
                LocalTime horaSaida = saida.toLocalTime();
                boolean entradaOk = !horaEntrada.isBefore(limiteInicio) && horaEntrada.isBefore(limiteFim);
                boolean saidaOk = !horaSaida.isBefore(limiteInicio) && (horaSaida.isBefore(limiteFim) || horaSaida.equals(limiteFim));
                return entradaOk && saidaOk && entrada.toLocalDate().isEqual(saida.toLocalDate());
            default:
                return false;
        }
    }

    public String getPrefixo() { return prefixo; }
    public double getVelocidadeCruzeiro() { return velocidadeCruzeiro; }
    public double getAutonomiaKm() { return autonomiaKm; }
    public CategoriaAeronave getCategoria() { return categoria; }
    public String getEmpresaManutencao() { return empresaManutencao; }
    public void setEmpresaManutencao(String e) { this.empresaManutencao = e; }
    public String getCompanhiaAerea() { return companhiaAerea; }
    public void setCompanhiaAerea(String c) { this.companhiaAerea = c; }
    public int getCapacidadePassageiros() { return capacidadePassageiros; }
    public void setCapacidadePassageiros(int p) { this.capacidadePassageiros = p; }
    public double getCapacidadeCargaTon() { return capacidadeCargaTon; }
    public void setCapacidadeCargaTon(double c) { this.capacidadeCargaTon = c; }
}
