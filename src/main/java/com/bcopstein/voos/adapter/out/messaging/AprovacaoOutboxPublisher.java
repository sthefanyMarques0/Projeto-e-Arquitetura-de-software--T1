package com.bcopstein.voos.adapter.out.messaging;

import com.bcopstein.voos.adapter.out.persistence.entity.AprovacaoOutboxEntity;
import com.bcopstein.voos.adapter.out.persistence.repository.AprovacaoOutboxJpaRepository;
import com.bcopstein.voos.config.AprovacaoRabbitConfig;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class AprovacaoOutboxPublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(AprovacaoOutboxPublisher.class);
    private final AprovacaoOutboxJpaRepository outbox;
    private final RabbitTemplate rabbit;

    public AprovacaoOutboxPublisher(AprovacaoOutboxJpaRepository outbox, RabbitTemplate rabbit) {
        this.outbox = outbox;
        this.rabbit = rabbit;
    }

    @Scheduled(fixedDelayString = "${planos.outbox.intervalo-ms:5000}")
    public void publicarPendentes() {
        for (AprovacaoOutboxEntity event : outbox.findTop50ByPublicadoEmIsNullOrderByCriadoEmAsc()) {
            try {
                event.registrarTentativa();
                outbox.save(event);
                Message message = MessageBuilder.withBody(event.getPayload().getBytes(StandardCharsets.UTF_8))
                        .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                        .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                        .setMessageId(event.getEventoId())
                        .build();
                rabbit.invoke(operations -> {
                    operations.send(AprovacaoRabbitConfig.EXCHANGE, AprovacaoRabbitConfig.ROUTING_KEY, message);
                    operations.waitForConfirmsOrDie(10_000);
                    return null;
                });
                event.marcarPublicado(LocalDateTime.now());
                outbox.save(event);
            } catch (RuntimeException e) {
                LOGGER.error("Não foi possível confirmar a publicação do evento {} do plano {}. Será tentado novamente.",
                        event.getEventoId(), event.getPlanoId(), e);
            }
        }
    }
}
