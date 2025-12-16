package com.fitness.activityservice.config;

import org.springframework.amqp.core.Binding; // Correct Import
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;   // Correct Import
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
        // Now this works because it refers to the Spring AMQP Queue class
        return new Queue(queue, true);
    }

    @Bean
    public DirectExchange activityExchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    public Binding activityBinding(Queue activityQueue, DirectExchange activityExchange) {
        return BindingBuilder.bind(activityQueue).to(activityExchange).with(routingKey);
    }
}