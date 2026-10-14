package com.bcopstein.voos.domain.service;

import com.bcopstein.voos.domain.model.aeronave.Aeronave;
import com.bcopstein.voos.domain.model.plano.PlanoDeVoo;
import com.bcopstein.voos.domain.model.plano.Trecho;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Controle de autonomia: a distância desde o último abastecimento não pode passar de 85%
 * da autonomia nominal. Sobrevoo acumula distância; pouso reabastece e zera o acumulado.
 */
public class ServicoAutonomia {

    public List<String> verificar(PlanoDeVoo plano) {
        List<String> erros = new ArrayList<>();
        Aeronave aeronave = plano.getAeronave();
        BigDecimal limite = aeronave.getAutonomiaSeguraKm();
        BigDecimal acumulada = BigDecimal.ZERO;
        List<Trecho> trechos = plano.getTrechos();
        for (int i = 0; i < trechos.size(); i++) {
            Trecho trecho = trechos.get(i);
            acumulada = acumulada.add(trecho.getAerovia().getDistanciaKm());
            if (acumulada.compareTo(limite) > 0) {
                erros.add(String.format("Trecho %d (%s): distância desde o último abastecimento (%s km) excede 85%% da autonomia da aeronave %s (%s km).",
                        i + 1, trecho.getAerovia().getId(), acumulada.stripTrailingZeros().toPlainString(),
                        aeronave.getPrefixo(), limite.stripTrailingZeros().toPlainString()));
                acumulada = BigDecimal.ZERO; // evita repetir o mesmo erro nos trechos seguintes
            }
            if (trecho.isPouso()) {
                acumulada = BigDecimal.ZERO;
            }
        }
        return erros;
    }
}
