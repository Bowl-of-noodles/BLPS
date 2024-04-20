package com.javadevjournal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javadevjournal.messaging.CheckMessage;
import com.javadevjournal.messaging.myConverter;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@EnableJms
public class UsefulService {

    @Value("${core.queue.name}")
    private String responseQueue;

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;
    private final myConverter myConverter;



    private final static Logger LOGGER = LoggerFactory.getLogger(UsefulService.class);

    @JmsListener(destination = "${second.queue.name}")
    public void processPoster(Message message) throws IOException, JMSException {
        //String data = message.getBody(String.class);
        //Offer offer = objectMapper.readValue(data, Offer.class);

        TextMessage msg = (TextMessage) message;
        LOGGER.info("following message is received: " + msg.getText());
        JSONObject jo = new JSONObject(msg.getText());
        String description = jo.getString("description");
        Long adId = jo.getLong("ad_id");

        String result = checkForSwearing(description);
        CheckMessage messageCheck = new CheckMessage(adId, result);
        String convertedMessage = myConverter.convertAdMessage(messageCheck);

        jmsTemplate.convertAndSend(responseQueue, convertedMessage);

    }


    private String checkForSwearing(String description){
        //boolean contains = description.contains("кот");

        //boolean check = description.matches("(.*)блять(.*)");
        //boolean check2 = description.matches("(\\s)кот(\\s*)");
        String regex = "\\bкот\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(description);
        if (matcher.find()) {
            return "unacceptable";
        }

        if(description.matches("(.*)блять(.*)")){
            return "unacceptable";
        }
        else if(description.contains(" кот") || description.contains(" кот ")){
            return "unacceptable";
        }
        else{
            return "acceptable";
        }
    }

}
