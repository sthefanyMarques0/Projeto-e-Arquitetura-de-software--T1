package com.bcopstein.voos.servico;

import com.bcopstein.voos.dominio.*;
import org.springframework.stereotype.Service;

@Service
public class ServicoAutonomia {

    public double distanciaAcumulada(PlanoDeVoo plano) {
        double distAcumulada = 0;
        for (Trecho t : plano.getTrechos()) {
            distAcumulada += t.getAerovia().getDistanciaKm();
        }
        return distAcumulada;
    }

    public boolean dentroDaAutonomia(Aeronave aeronave, double distancia) {
        return distancia <= aeronave.getAutonomiaSegura();
    }
}
