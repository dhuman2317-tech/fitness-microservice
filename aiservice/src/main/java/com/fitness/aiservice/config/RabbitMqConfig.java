package com.fitness.aiservice.config;

import org.springframework.amqp.core.Binding; // Corrected import
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;    // Corrected import
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${rabbitmq.queue.name}")
    private String queue;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @Bean
    public Queue activityQueue() {
        // Uses org.springframework.amqp.core.Queue constructor
        return new Queue(queue, true); // durable = true, non-auto-delete
    }

    @Bean
    public DirectExchange activityExchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    public Binding activityBinding(Queue activityQueue, DirectExchange activityExchange) {
        // activityQueue and activityExchange are now correctly typed as Spring AMQP objects
        return BindingBuilder.bind(activityQueue).to(activityExchange).with(routingKey);
    }
}