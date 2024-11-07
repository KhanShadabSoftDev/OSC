package com.osc.recentlyviewedservice.kafka.producer;

import com.osc.avro.RecentViewHistory;
import com.osc.recentlyviewedservice.config.ApplicationConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class RecordProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTemplate<String, Object> objectKafkaTemplate;

    private static final Logger log = LogManager.getLogger(RecordProducer.class);

    // Constructor Injection with Qualifier for the Avro producer
    public RecordProducer(KafkaTemplate<String, String> kafkaTemplate,
                          @Qualifier("objectKafkaTemplate") KafkaTemplate<String, Object> objectKafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectKafkaTemplate = objectKafkaTemplate;
    }

    // Method to send recently viewed product history to Product-View-Topic
    public void sendRecentViewHistoryToPVTopic(String userId, String productId) {

        log.info("Produced RecentlyViewed Product to ProductViewTopic for UserId:{}",userId);
        kafkaTemplate.send(ApplicationConfig.PRODUCT_VIEW_TOPIC, userId, productId);
    }


//    Method to send recently viewed product history to Recently-Viewed-Product-Topic
    public void sendRecentViewHistoryToRVTopic(String userId, RecentViewHistory recentViewHistory) {
        log.info("Produced RecentlyViewed Product to RVTopic for UserId:{}",userId);
        objectKafkaTemplate.send(ApplicationConfig.PRODUCT_VIEW_TOPIC, userId, recentViewHistory);
    }




}
