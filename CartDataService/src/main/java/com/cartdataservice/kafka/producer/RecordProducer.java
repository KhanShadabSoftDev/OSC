package com.cartdataservice.kafka.producer;

import com.cartdataservice.config.ApplicationConfig;
import com.osc.avro.CartList;
import com.osc.avro.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecordProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public RecordProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    // Method to send recently viewed product history to Kafka
    public void sendCartDetailsToKafka(String userId, List<Product> userCartProducts) {
        // Create CartList from the list of products
        CartList cartList = CartList.newBuilder()
                .setProducts(userCartProducts)
                .build();
        kafkaTemplate.send(ApplicationConfig.CART_TOPIC, userId, cartList);
    }




}
