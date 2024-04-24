package com.javadevjournal.delegates;

import com.javadevjournal.jpa.entity.Ad;
import com.javadevjournal.jpa.enums.AdStatus;
import com.javadevjournal.jpa.repository.AdsRepository;
import com.javadevjournal.messaging.myConverter;
import com.javadevjournal.service.AdsService;
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
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Named
public class SaveCheckedAdDelegate implements JavaDelegate {
    private static final Logger logger = Logger.getLogger(CreateAdDelegate.class.getName());
    private final AdsRepository adsRepository;
    private final RuntimeService runtimeService;
    private final JmsTemplate jmsTemplate;
    private final com.javadevjournal.messaging.myConverter myConverter;

    public SaveCheckedAdDelegate(AdsRepository adsRepository, RuntimeService runtimeService, JmsTemplate jmsTemplate, myConverter myConverter) {
        this.adsRepository = adsRepository;
        this.runtimeService = runtimeService;
        this.jmsTemplate = jmsTemplate;
        this.myConverter = myConverter;
    }

    @JmsListener(destination = "${core.queue.name}")
    public void receiveProcessedPoster(Message message) throws JMSException, IOException {
        TextMessage msg = (TextMessage) message;
        //LOGGER.info("following message is received: " + msg.getText());
        logger.log(Level.INFO, "following message is received: " + msg.getText());
        JSONObject jo = new JSONObject(msg.getText());
        Map<String, Object> variables = new HashMap<>();
        variables.put("request", jo);
        runtimeService.startProcessInstanceByMessage("ReceiveCheckResult", variables);
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        var jo = (JSONObject) execution.getVariable("request");
        //LOGGER.info("following message is received: " + msg.getText());

        String checkResult = jo.getString("check_result");
        Long id = jo.getLong("ad_id");

        Optional<Ad> adOpt = adsRepository.findById(id);
        Ad ad = adOpt.get();
        if(checkResult.equals("acceptable")){
            ad.setStatus(AdStatus.APPROVED);
        }
        else{
            ad.setStatus(AdStatus.BANNED);
        }
        adsRepository.save(ad);
    }
}
