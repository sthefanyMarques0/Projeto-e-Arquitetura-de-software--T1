package com.bcopstein.registro;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
    @Bean
    Queue approvalQueue() { return new Queue("aprovacoes.queue", true); }

    @Bean
    TopicExchange approvalExchange() { return new TopicExchange("aprovacoes.exchange", true, false); }

    @Bean
    Binding approvalBinding(Queue approvalQueue, TopicExchange approvalExchange) {
        return BindingBuilder.bind(approvalQueue).to(approvalExchange).with("aprovacao.confirmada");
    }
}
