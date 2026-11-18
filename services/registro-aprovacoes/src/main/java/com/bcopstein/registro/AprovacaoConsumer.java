package com.bcopstein.registro;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.springframework.amqp.core.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AprovacaoConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(AprovacaoConsumer.class);
    private final ObjectMapper objectMapper;
    private final AprovacaoRepository repository;

    public AprovacaoConsumer(ObjectMapper objectMapper, AprovacaoRepository repository) {
        this.objectMapper = objectMapper;
        this.repository = repository;
    }

    @RabbitListener(queues = "aprovacoes.queue")
    @Transactional
    public void receber(Message message) {
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);
        try {
            AprovacaoEvent event = objectMapper.readValue(payload, AprovacaoEvent.class);
            if (event.eventoId() == null || event.planoId() == null || event.versaoContrato() == null) {
                throw new IllegalArgumentException("Evento de aprovação sem identificadores ou versão do contrato.");
            }
            if (repository.existsById(event.eventoId()) || repository.findByPlanoId(event.planoId()).isPresent()) {
                LOGGER.info("Reentrega idempotente ignorada para plano {} (evento {}).", event.planoId(), event.eventoId());
                return;
            }
            repository.saveAndFlush(new AprovacaoEntity(event.eventoId(), event.planoId(),
                    java.time.LocalDateTime.now(), payload));
            LOGGER.info("Aprovação do plano {} registrada; evento {}.", event.planoId(), event.eventoId());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Mensagem de aprovação não contém JSON válido.", e);
        }
    }
}
