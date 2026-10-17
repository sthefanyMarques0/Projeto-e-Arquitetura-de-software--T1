package com.bcopstein.voos.domain.model.rede;

import com.bcopstein.voos.domain.exception.DadosInvalidosException;
import com.bcopstein.voos.domain.model.aeronave.Aeronave;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/** Ligação orientada entre dois aeroportos. Imutável após o cadastro. */
public class Aerovia {
    public static final int ALTITUDE_MINIMA = 25_000;
    public static final int ALTITUDE_MAXIMA = 35_000;
    public static final int INCREMENTO_ALTITUDE = 1_000;

    private final String id;
    private final Aeroporto origem;
    private final Aeroporto destino;
    private final BigDecimal distanciaKm;

    public Aerovia(String id, Aeroporto origem, Aeroporto destino, BigDecimal distanciaKm) {
        if (id == null || id.isBlank() || origem == null || destino == null) {
            throw new DadosInvalidosException("Aerovia deve ter identificador, origem e destino.");
        }
        if (distanciaKm == null || distanciaKm.signum() <= 0) {
            throw new DadosInvalidosException("Distância da aerovia deve ser positiva.");
        }
        this.id = id;
        this.origem = origem;
        this.destino = destino;
        this.distanciaKm = distanciaKm;
    }

    /** Altitudes de 25.000 a 35.000 pés, inclusive, em incrementos de 1.000 pés. */
    public static boolean altitudeNaFaixa(int altitudePes) {
        return altitudePes >= ALTITUDE_MINIMA && altitudePes <= ALTITUDE_MAXIMA
                && altitudePes % INCREMENTO_ALTITUDE == 0;
    }

    public static List<Integer> altitudesPrevistas() {
        List<Integer> altitudes = new ArrayList<>();
        for (int a = ALTITUDE_MINIMA; a <= ALTITUDE_MAXIMA; a += INCREMENTO_ALTITUDE) {
            altitudes.add(a);
        }
        return altitudes;
    }

    public Duration duracaoPara(Aeronave aeronave) {
        return aeronave.tempoDeVoo(distanciaKm);
    }

    public String getId() { return id; }

    public Aeroporto getOrigem() { return origem; }

    public Aeroporto getDestino() { return destino; }

    public BigDecimal getDistanciaKm() { return distanciaKm; }
}
