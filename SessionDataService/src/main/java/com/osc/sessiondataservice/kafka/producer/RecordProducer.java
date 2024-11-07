package com.osc.sessiondataservice.kafka.producer;

import com.osc.avro.Session;
import com.osc.avro.SessionKey;
import com.osc.sessiondataservice.config.KafkaConfiguration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class RecordProducer {

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    public RecordProducer(KafkaTemplate<Object, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void producerSession(SessionKey key, Session session) {
        kafkaTemplate.send(KafkaConfiguration.SESSION_TOPIC,key,session);
    }


}
