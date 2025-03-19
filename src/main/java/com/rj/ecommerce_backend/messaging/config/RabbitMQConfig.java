package com.rj.ecommerce_backend.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
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


    // Payment Intent Creation
    public static final String PAYMENT_INTENT_QUEUE = "payment-intent-queue";
    public static final String PAYMENT_INTENT_EXCHANGE = "payment-intent-exchange";
    public static final String PAYMENT_INTENT_ROUTING_KEY = "payment-intent-routing-key";

    // Payment Intent Response
    public static final String PAYMENT_RESPONSE_QUEUE = "payment-response-queue";
    public static final String PAYMENT_RESPONSE_EXCHANGE = "payment-response-exchange";
    public static final String PAYMENT_RESPONSE_ROUTING_KEY = "payment-response-routing-key";

    // Payment Verification
    public static final String PAYMENT_VERIFICATION_QUEUE = "payment-verification-queue";
    public static final String PAYMENT_VERIFICATION_EXCHANGE = "payment-verification-exchange";
    public static final String PAYMENT_VERIFICATION_ROUTING_KEY = "payment-verification-routing-key";

    // Payment Verification Response
    public static final String VERIFICATION_RESPONSE_QUEUE = "verification-response-queue";
    public static final String VERIFICATION_RESPONSE_EXCHANGE = "verification-response-exchange";
    public static final String VERIFICATION_RESPONSE_ROUTING_KEY = "verification-response-routing-key";


    @Bean
    public TopicExchange paymentVerificationExchange() {
        return new TopicExchange(PAYMENT_VERIFICATION_EXCHANGE, true, false);
    }
    @Bean
    public Queue paymentVerificationQueue() {
        return new Queue(PAYMENT_VERIFICATION_QUEUE, true);
    }
    @Bean
    public Binding paymentVerificationBinding() {
        return BindingBuilder
                .bind(paymentVerificationQueue())
                .to(paymentVerificationExchange())
                .with(PAYMENT_VERIFICATION_ROUTING_KEY);
    }

    @Bean
    public TopicExchange verificationResponseExchange() {
        return new TopicExchange(VERIFICATION_RESPONSE_EXCHANGE, true, false);
    }

    @Bean
    public Queue verificationResponseQueue() {
        return new Queue(VERIFICATION_RESPONSE_QUEUE, true);
    }

    @Bean
    public Binding verificationResponseBinding() {
        return BindingBuilder
                .bind(verificationResponseQueue())
                .to(verificationResponseExchange())
                .with(VERIFICATION_RESPONSE_ROUTING_KEY);
    }

    @Bean
    public TopicExchange paymentIntentExchange() {
        return new TopicExchange(PAYMENT_INTENT_EXCHANGE, true, false);
    }

    @Bean
    public Queue paymentIntentQueue() {
        return new Queue(PAYMENT_INTENT_QUEUE, true);
    }

    @Bean
    public Binding paymentIntentBinding() {
        return BindingBuilder
                .bind(paymentIntentQueue())
                .to(paymentIntentExchange())
                .with(PAYMENT_INTENT_ROUTING_KEY);
    }

    @Bean
    public Queue paymentResponseQueue() {
        return new Queue(PAYMENT_RESPONSE_QUEUE, true);
    }

    @Bean
    public TopicExchange paymentResponseExchange() {
        return new TopicExchange(PAYMENT_RESPONSE_EXCHANGE, true, false);
    }

    @Bean
    public Binding paymentResponseBinding() {
        return BindingBuilder
                .bind(paymentResponseQueue())
                .to(paymentResponseExchange())
                .with(PAYMENT_RESPONSE_ROUTING_KEY);
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

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
