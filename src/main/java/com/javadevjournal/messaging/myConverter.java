package com.javadevjournal.messaging;

import org.springframework.stereotype.Component;

@Component
public class myConverter{

    public String convertAdMessage(CheckMessage checkMessage) {
        return String.format("{'message':'ad_check', 'ad_id':'%s', 'check_result':'%s'}",
                    checkMessage.getAdId(), checkMessage.getResult());
    }
}
