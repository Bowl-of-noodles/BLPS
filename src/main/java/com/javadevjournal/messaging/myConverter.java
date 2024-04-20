package com.javadevjournal.messaging;


import org.springframework.stereotype.Component;

@Component
public class myConverter{

    public String convertAdMessage(AdMessage adMessage) {
        return String.format("{'message':'ad_check', 'ad_id':'%s', 'description':'%s'}",
                adMessage.getAdId(), adMessage.getDescription());
    }
}