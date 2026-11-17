package com.bcopstein.voos.domain.model.aeronave;

import com.bcopstein.voos.domain.exception.DadosInvalidosException;
import com.bcopstein.voos.domain.model.tempo.Periodo;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

/**
 * Aeronave genérica. Cada categoria é uma subclasse que define suas próprias regras de
 * operação ({@link #permiteOperar}). Uma nova categoria entra como nova subclasse, sem
 * alterar a validação dos planos (princípio aberto/fechado).
 */
public abstract class Aeronave {
    private static final BigDecimal FATOR_AUTONOMIA_SEGURA = new BigDecimal("0.85");
    private static final BigDecimal SEGUNDOS_POR_HORA = BigDecimal.valueOf(3600);

    private final String prefixo;
    private final BigDecimal velocidadeCruzeiroKmH;
    private final BigDecimal autonomiaNominalKm;

    protected Aeronave(String prefixo, BigDecimal velocidadeCruzeiroKmH, BigDecimal autonomiaNominalKm) {
        if (prefixo == null || prefixo.isBlank()) {
            throw new DadosInvalidosException("Aeronave deve ter prefixo.");
        }
        if (velocidadeCruzeiroKmH == null || velocidadeCruzeiroKmH.signum() <= 0) {
            throw new DadosInvalidosException("Velocidade de cruzeiro deve ser positiva.");
        }
        if (autonomiaNominalKm == null || autonomiaNominalKm.signum() <= 0) {
            throw new DadosInvalidosException("Autonomia nominal deve ser positiva.");
        }
        this.prefixo = prefixo;
        this.velocidadeCruzeiroKmH = velocidadeCruzeiroKmH;
        this.autonomiaNominalKm = autonomiaNominalKm;
    }

    public abstract CategoriaAeronave getCategoria();

    /** Regra específica da categoria para operar na altitude durante todo o período. */
    public abstract boolean permiteOperar(int altitudePes, Periodo periodo);

    /** Descrição da restrição da categoria, usada nas mensagens de validação. */
    public abstract String descricaoRestricao();

    /** Distância máxima sem reabastecimento: 85% da autonomia nominal. */
    public BigDecimal getAutonomiaSeguraKm() {
        return autonomiaNominalKm.multiply(FATOR_AUTONOMIA_SEGURA);
    }

    /** Duração = ceil(distância / velocidade × 3600) segundos (seção 4.1 do enunciado). */
    public Duration tempoDeVoo(BigDecimal distanciaKm) {
        BigDecimal segundos = distanciaKm.multiply(SEGUNDOS_POR_HORA)
                .divide(velocidadeCruzeiroKmH, 0, RoundingMode.CEILING);
        return Duration.ofSeconds(segundos.longValueExact());
    }

    public String getPrefixo() { return prefixo; }

    public BigDecimal getVelocidadeCruzeiroKmH() { return velocidadeCruzeiroKmH; }

    public BigDecimal getAutonomiaNominalKm() { return autonomiaNominalKm; }
}
