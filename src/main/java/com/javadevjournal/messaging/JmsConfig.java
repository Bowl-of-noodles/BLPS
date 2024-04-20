package com.javadevjournal.messaging;


import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.*;

@Configuration
public class JmsConfig {

    @Value("${second.queue.name}")
    private String secondQueue;

    @Value("${core.queue.name}")
    private String coreQueue;


    @Bean
    public ActiveMQConnectionFactory jmsConnectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL("tcp://localhost:61616/");
        connectionFactory.setUserName("admin");
        connectionFactory.setPassword("admin");
        return connectionFactory;
    }

    @Bean
    public JmsTemplate defaultJmsTemplate() {
        return new JmsTemplate(jmsConnectionFactory());
    }

    /*@Bean
    public Destination destinationToListen(ConnectionFactory connectionFactory) throws JMSException {
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession();
        Destination jmsDestination = session.createQueue(coreQueue);
        return jmsDestination;
    }

    @Bean
    public Destination destinationToSend(ConnectionFactory connectionFactory) throws JMSException {
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession();
        Destination jmsDestination = session.createQueue(secondQueue);
        return jmsDestination;
    }*/

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ActiveMQConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        //factory.setMessageConverter(adtMessageConverter());
        //factory.setConcurrency("1");
        return factory;
    }

    /*@Bean
    public MessageConverter adtMessageConverter() {
        return new myConverter();
    }*/

}
