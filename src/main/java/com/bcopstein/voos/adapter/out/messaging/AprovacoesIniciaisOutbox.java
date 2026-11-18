package com.bcopstein.voos.adapter.out.messaging;

import com.bcopstein.voos.application.port.out.AprovacaoOutboxPort;
import com.bcopstein.voos.application.port.out.PlanoDeVooPort;
import com.bcopstein.voos.application.port.out.RelogioPort;
import com.bcopstein.voos.domain.model.plano.PlanoDeVoo;
import com.bcopstein.voos.domain.model.plano.SituacaoPlano;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class AprovacoesIniciaisOutbox {
    private static final Logger LOGGER = LoggerFactory.getLogger(AprovacoesIniciaisOutbox.class);
    private final PlanoDeVooPort planos;
    private final AprovacaoOutboxPort outbox;
    private final RelogioPort relogio;

    public AprovacoesIniciaisOutbox(PlanoDeVooPort planos, AprovacaoOutboxPort outbox, RelogioPort relogio) {
        this.planos = planos;
        this.outbox = outbox;
        this.relogio = relogio;
    }

    @Scheduled(initialDelay = 15_000, fixedDelay = 60_000)
    public void registrarAprovacoesDaCargaInicial() {
        try {
            for (PlanoDeVoo plano : planos.listarPorSituacao(SituacaoPlano.APROVADO)) {
                String eventId = UUID.nameUUIDFromBytes(
                        ("plano-aprovado-legado-v1:" + plano.getId()).getBytes(StandardCharsets.UTF_8)).toString();
                if (!outbox.contem(eventId)) {
                    outbox.registrar(eventId, plano, relogio.agora());
                }
            }
        } catch (RuntimeException e) {
            LOGGER.error("Não foi possível preparar os registros históricos de aprovação; nova tentativa será feita.", e);
        }
    }
}
