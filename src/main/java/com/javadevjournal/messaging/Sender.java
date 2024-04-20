package com.javadevjournal.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javadevjournal.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.*;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class Sender {
    @Value("${second.queue.name}")
    private String secondQueue;
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;
    private final OfferService offerService;
    private final myConverter myConverter;
    private final static Logger LOGGER = LoggerFactory.getLogger(Sender.class);



    public void sendMessage(Long offerId) throws JMSException, IOException {

        AdMessage offerM = new AdMessage();
        offerM.setAdId(Long.getLong("1"));
        offerM.setDescription("Product_test");
        String message = myConverter.convertAdMessage(offerM);

        jmsTemplate.convertAndSend(secondQueue, message);
    }



    /*@JmsListener(destination = "${core.queue.name}")
    public void recieveMessage(Message message){
        LOGGER.info("Message: {}", message);
        LOGGER.debug("Message: {}", message);
    }*/
}