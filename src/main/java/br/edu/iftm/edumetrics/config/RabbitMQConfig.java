package br.edu.iftm.edumetrics.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE = "edumetrics.exchange";
    public static final String FILA_RELATORIOS = "relatorios.queue";
    public static final String FILA_DLQ = "relatorios.dlq";
    public static final String ROUTING_KEY_RELATORIOS = "relatorios.gerar";
    public static final String ROUTING_KEY_DLQ = "relatorios.dlq";

    @Bean
    public DirectExchange edumetricsExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue relatoriosQueue() {
        return new Queue(FILA_RELATORIOS, true, false, false, Map.of(
            "x-dead-letter-exchange", EXCHANGE,
            "x-dead-letter-routing-key", ROUTING_KEY_DLQ
        ));
    }

    @Bean
    public Queue relatoriosDeadLetterQueue() {
        return new Queue(FILA_DLQ, true);
    }

    @Bean
    public Binding relatoriosBinding(Queue relatoriosQueue, DirectExchange edumetricsExchange) {
        return BindingBuilder.bind(relatoriosQueue)
            .to(edumetricsExchange)
            .with(ROUTING_KEY_RELATORIOS);
    }

    @Bean
    public Binding relatoriosDlqBinding(Queue relatoriosDeadLetterQueue, DirectExchange edumetricsExchange) {
        return BindingBuilder.bind(relatoriosDeadLetterQueue)
            .to(edumetricsExchange)
            .with(ROUTING_KEY_DLQ);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}
