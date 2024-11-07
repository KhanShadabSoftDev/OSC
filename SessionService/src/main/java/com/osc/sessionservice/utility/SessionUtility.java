package com.osc.sessionservice.utility;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class SessionUtility {


    public static String generateSessionId() {
        Random random = new Random();
        int sessionId = 100000 + random.nextInt(900000);

        return String.valueOf(sessionId);
    }


}
