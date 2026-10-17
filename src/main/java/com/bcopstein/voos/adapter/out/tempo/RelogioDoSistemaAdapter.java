package com.bcopstein.voos.adapter.out.tempo;

import com.bcopstein.voos.application.port.out.RelogioPort;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Instante atual no fuso operacional America/Sao_Paulo, independente do fuso do servidor.
 * Para demonstrações e testes, a propriedade "planos.relogio.agora" fixa o instante
 * (ex.: planos.relogio.agora=2026-11-20T09:00:00).
 */
@Component
public class RelogioDoSistemaAdapter implements RelogioPort {
    public static final ZoneId FUSO_OPERACIONAL = ZoneId.of("America/Sao_Paulo");

    private final LocalDateTime instanteFixo;

    public RelogioDoSistemaAdapter(@Value("${planos.relogio.agora:}") String instanteFixo) {
        this.instanteFixo = instanteFixo == null || instanteFixo.isBlank() ? null : LocalDateTime.parse(instanteFixo);
    }

    @Override
    public LocalDateTime agora() {
        if (instanteFixo != null) {
            return instanteFixo;
        }
        return LocalDateTime.now(FUSO_OPERACIONAL).truncatedTo(ChronoUnit.SECONDS);
    }
}
