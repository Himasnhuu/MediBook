package com.medibook.notification.security;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange mediBookExchange() {
        return new TopicExchange(RabbitMQConstants.EXCHANGE);
    }

    @Bean public Queue queueAppointmentBooked() {
        return new Queue(RabbitMQConstants.QUEUE_APPOINTMENT_BOOKED, true);
    }
    @Bean public Queue queueAppointmentCancelled() {
        return new Queue(RabbitMQConstants.QUEUE_APPOINTMENT_CANCELLED, true);
    }
    @Bean public Queue queueAppointmentCompleted() {
        return new Queue(RabbitMQConstants.QUEUE_APPOINTMENT_COMPLETED, true);
    }
    @Bean public Queue queuePaymentSuccess() {
        return new Queue(RabbitMQConstants.QUEUE_PAYMENT_SUCCESS, true);
    }
    @Bean public Queue queuePaymentRefunded() {
        return new Queue(RabbitMQConstants.QUEUE_PAYMENT_REFUNDED, true);
    }
    @Bean public Queue queueReviewSubmitted() {
        return new Queue(RabbitMQConstants.QUEUE_REVIEW_SUBMITTED, true);
    }

    @Bean public Binding bindingAppointmentBooked() {
        return BindingBuilder.bind(queueAppointmentBooked())
            .to(mediBookExchange()).with(RabbitMQConstants.APPOINTMENT_BOOKED);
    }
    @Bean public Binding bindingAppointmentCancelled() {
        return BindingBuilder.bind(queueAppointmentCancelled())
            .to(mediBookExchange()).with(RabbitMQConstants.APPOINTMENT_CANCELLED);
    }
    @Bean public Binding bindingAppointmentCompleted() {
        return BindingBuilder.bind(queueAppointmentCompleted())
            .to(mediBookExchange()).with(RabbitMQConstants.APPOINTMENT_COMPLETED);
    }
    @Bean public Binding bindingPaymentSuccess() {
        return BindingBuilder.bind(queuePaymentSuccess())
            .to(mediBookExchange()).with(RabbitMQConstants.PAYMENT_SUCCESS);
    }
    @Bean public Binding bindingPaymentRefunded() {
        return BindingBuilder.bind(queuePaymentRefunded())
            .to(mediBookExchange()).with(RabbitMQConstants.PAYMENT_REFUNDED);
    }
    @Bean public Binding bindingReviewSubmitted() {
        return BindingBuilder.bind(queueReviewSubmitted())
            .to(mediBookExchange()).with(RabbitMQConstants.REVIEW_SUBMITTED);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}