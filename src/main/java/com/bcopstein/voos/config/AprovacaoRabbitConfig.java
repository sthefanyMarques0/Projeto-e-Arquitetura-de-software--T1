package com.bcopstein.voos.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AprovacaoRabbitConfig {
    public static final String EXCHANGE = "aprovacoes.exchange";
    public static final String QUEUE = "aprovacoes.queue";
    public static final String ROUTING_KEY = "aprovacao.confirmada";

    @Bean
    Queue aprovacoesQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    TopicExchange aprovacoesExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    Binding aprovacoesBinding(Queue aprovacoesQueue, TopicExchange aprovacoesExchange) {
        return BindingBuilder.bind(aprovacoesQueue).to(aprovacoesExchange).with(ROUTING_KEY);
    }
}
