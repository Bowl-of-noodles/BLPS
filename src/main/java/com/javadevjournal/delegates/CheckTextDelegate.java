package com.javadevjournal.delegates;

import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.messaging.AdMessage;
import com.javadevjournal.messaging.myConverter;
import com.javadevjournal.security.JwtUtil;
import com.javadevjournal.service.AdsService;
import com.javadevjournal.service.CustomerService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.json.JSONObject;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.inject.Named;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Named
public class CheckTextDelegate implements JavaDelegate {
    private static final Logger logger = Logger.getLogger(CheckTextDelegate.class.getName());
    private final AdsService adsService;
    private final RuntimeService runtimeService;
    private final JmsTemplate jmsTemplate;
    private final myConverter myConverter;

    public CheckTextDelegate(AdsService adsService, RuntimeService runtimeService, JmsTemplate jmsTemplate, myConverter myConverter) {
        this.adsService = adsService;
        this.runtimeService = runtimeService;
        this.jmsTemplate = jmsTemplate;
        this.myConverter = myConverter;
    }

    @JmsListener(destination = "${second.queue.name}")
    public void checkMessage(Message message) throws JMSException, IOException {
        TextMessage msg = (TextMessage) message;
        //LOGGER.info("following message is received: " + msg.getText());
        logger.log(Level.INFO, "following message is received: " + msg.getText());
        JSONObject jo = new JSONObject(msg.getText());
        //String description = jo.getString("description");
        //Long adId = jo.getLong("ad_id");
        //AdMessage message1 = new AdMessage(adId, description);
        Map<String, Object> variables = new HashMap<>();
        variables.put("request", jo);
        runtimeService.startProcessInstanceByMessage("CheckMessage", variables);
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        var jo = (JSONObject) execution.getVariable("request");

        String description = jo.getString("description");
        Long adId = jo.getLong("ad_id");
        String result = checkForSwearing(description);
        AdMessage messageCheck = new AdMessage(adId, result);
        String convertedMessage = myConverter.convertAdMessage(messageCheck);

        jmsTemplate.convertAndSend("core-queue", convertedMessage);
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
