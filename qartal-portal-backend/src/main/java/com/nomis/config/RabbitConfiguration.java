package com.nomis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nomis.service.PropertyService;
import javax.inject.Inject;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


/**
 * @author Sokolov
 */
@Configuration
@EnableRabbit
@ComponentScan(
    value = {"com.nomis"})
public class RabbitConfiguration {


  @Inject
  PropertyService propertyService;

  @Bean
  public ConnectionFactory rabbitConnectionFactory() {
    CachingConnectionFactory connectionFactory = new CachingConnectionFactory(
        propertyService.getAmqpHost());
    connectionFactory.setCacheMode(
        CachingConnectionFactory.CacheMode.CHANNEL); // Mandatory option. Only this cash mode permit auto-declaring of queues in rabbitMq broker.
    connectionFactory.setPort(propertyService.getAmqpPort());
    connectionFactory.setUsername(propertyService.getAmqpUser());
    connectionFactory.setPassword(propertyService.getAmqpPassword());

    return connectionFactory;
  }

  @Bean
  public String logQueue() {
    return propertyService.getLogQueue();
  }

  @Bean
  public String exchangeName() {
    return propertyService.getTopicExchangeName();
  }

  @Bean
  public AmqpAdmin rabbitAdmin(ConnectionFactory factory) {
    AmqpAdmin admin = new RabbitAdmin(factory);
    return admin;
  }

  @Bean
  public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
      ConnectionFactory connectionFactory) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setMessageConverter(new Jackson2JsonMessageConverter());
    factory.setConnectionFactory(connectionFactory);
    factory.setConcurrentConsumers(5);
    factory.setMaxConcurrentConsumers(10);
    factory.setDefaultRequeueRejected(false);

    return factory;
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

}
