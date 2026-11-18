package com.bcopstein.registro;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AprovacaoConsumerTest {
    @Autowired AprovacaoConsumer consumer;
    @Autowired AprovacaoRepository repository;
    @Autowired ObjectMapper mapper;

    @Test
    void repeatedDeliveryCreatesOneHistoricalRecord() throws Exception {
        AprovacaoEvent event = new AprovacaoEvent("event-100", "1.0", 100L,
                LocalDateTime.of(2027, 1, 15, 9, 0), "usuaria-42",
                new AprovacaoEvent.Piloto(101L, "Cmte. Exemplo"),
                new AprovacaoEvent.Aeronave("PT-TEST", "PARTICULAR",
                        new java.math.BigDecimal("600"), new java.math.BigDecimal("2200"),
                        "Manutenção", null, null, null),
                "POA", "FLN", LocalDateTime.of(2027, 1, 15, 10, 0),
                List.of(new AprovacaoEvent.Trecho(1, "R1", "POA", "FLN",
                        new java.math.BigDecimal("363"), 26_000,
                        LocalDateTime.of(2027, 1, 15, 10, 0), LocalDateTime.of(2027, 1, 15, 10, 36),
                        "POUSO", 30, 1)));
        String payload = mapper.writeValueAsString(event);

        Message message = new Message(payload.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                new MessageProperties());
        consumer.receber(message);
        consumer.receber(message);

        assertEquals(1, repository.count());
        assertEquals(payload, repository.findByPlanoId(100L).orElseThrow().getPayload());
    }
}
