package com.osc.sessiondataservice.service;


import com.osc.avro.Session;
import com.osc.avro.SessionKey;
import com.osc.sessiondataservice.kafka.consumer.KafkaRecordService;
import com.osc.sessiondataservice.kafka.producer.RecordProducer;
import com.osc.sessiondataservice.utility.SessionUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final KafkaRecordService kafkaRecordService;

    private final RecordProducer producer;

    private final SessionUtility sessionUtility;



    public boolean getSessionStatus(SessionKey key) {
        Session session = kafkaRecordService.getSessionBySessionId(key);
        return  session!=null && session.getIsActive();
    }


    public void produceSession(String userId,String deviceName, Session session) {
        try {
//5.)  TODO Use same method for key which created before..
            SessionKey key = SessionKey.newBuilder()
                    .setUserId(userId)
                    .setDeviceId(deviceName)
                    .build();
//            String key =
//                    sessionUtility.createSessionTopicKey(userId, deviceName);

            this.producer.producerSession(key, session);
        } catch (Exception ex) {
//            Throw Kafka Exception
            throw new RuntimeException("Failed to Produce Session",ex);
        }
    }

}
