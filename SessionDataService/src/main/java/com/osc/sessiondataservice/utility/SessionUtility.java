package com.osc.sessiondataservice.utility;

import org.springframework.stereotype.Component;

@Component
public class SessionUtility {


    public String createSessionTopicKey(String userId, String deviceName) {

        return userId + "##" + deviceName;
    }

}





