package com.rj.ecommerce_backend.messaging.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EMAIL_EXCHANGE = "email.exchange";
    public static final String ROUTING_KEY = "email.routing.key";

    // Notification configuration
    public static final String EMAIL_NOTIFICATION_EXCHANGE = "email.notification.exchange";
    public static final String EMAIL_NOTIFICATION_QUEUE = "email.notification.queue";
    public static final String EMAIL_NOTIFICATION_ROUTING_KEY = "email.notification.routing.key";

    // Checkout Session Response
    public static final String CHECKOUT_SESSION_RESPONSE_QUEUE = "checkout-session-response-queue";
    public static final String CHECKOUT_SESSION_RESPONSE_EXCHANGE = "checkout-session-response-exchange";
    public static final String CHECKOUT_SESSION_RESPONSE_ROUTING_KEY = "checkout-session-response-routing-key";

    // Checkout Session
    public static final String CHECKOUT_SESSION_QUEUE = "checkout-session-queue";
    public static final String CHECKOUT_SESSION_EXCHANGE = "checkout-session-exchange";
    public static final String CHECKOUT_SESSION_ROUTING_KEY = "checkout-session-routing-key";


    @Bean
    public TopicExchange checkoutSessionExchange() {
        return new TopicExchange(CHECKOUT_SESSION_EXCHANGE, true, false);
    }

    @Bean
    public Queue checkoutSessionQueue() {
        return new Queue(CHECKOUT_SESSION_QUEUE, true);
    }

    @Bean
    public Binding checkoutSessionBinding() {
        return BindingBuilder
                .bind(checkoutSessionQueue())
                .to(checkoutSessionExchange())
                .with(CHECKOUT_SESSION_ROUTING_KEY);
    }

    @Bean
    public TopicExchange checkoutSessionResponseExchange() {
        return new TopicExchange(CHECKOUT_SESSION_RESPONSE_EXCHANGE, true, false);
    }

    @Bean
    public Queue checkoutSessionResponseQueue() {
        return new Queue(CHECKOUT_SESSION_RESPONSE_QUEUE, true);
    }

    @Bean
    public Binding checkoutSessionResponseBinding() {
        return BindingBuilder
                .bind(checkoutSessionResponseQueue())
                .to(checkoutSessionResponseExchange())
                .with(CHECKOUT_SESSION_RESPONSE_ROUTING_KEY);
    }


    // Add queue for receiving notifications
    @Bean
    public Queue emailNotificationQueue() {
        return new Queue(EMAIL_NOTIFICATION_QUEUE, true);
    }

    @Bean
    public TopicExchange emailNotificationExchange() {
        return new TopicExchange(EMAIL_NOTIFICATION_EXCHANGE, true, false);
    }

    @Bean
    public Binding emailNotificationBinding() {
        return BindingBuilder
                .bind(emailNotificationQueue())
                .to(emailNotificationExchange())
                .with(EMAIL_NOTIFICATION_ROUTING_KEY);
    }



    /**
     * Creates the MessageConverter using the specific rabbitObjectMapper bean.
     */
    @Bean
    public MessageConverter jsonMessageConverter(
            @Qualifier("rabbitObjectMapper") ObjectMapper rabbitObjectMapper) { // <<< Inject QUALIFIED bean
        return new Jackson2JsonMessageConverter(rabbitObjectMapper);
    }

    /**
     * Configures the RabbitTemplate to use the custom jsonMessageConverter.
     * Note: Ensure the MessageConverter bean is correctly named or qualified if needed.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter jsonMessageConverter) { // Spring injects the bean defined above
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }
}
