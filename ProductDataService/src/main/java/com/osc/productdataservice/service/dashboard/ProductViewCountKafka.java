package com.osc.productdataservice.service.dashboard;
import com.osc.avro.ProductViewCount;
import com.osc.productdataservice.kafka.consumer.KafkaRecordService;
import com.osc.productdataservice.kafka.producer.RecordProducer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class ProductViewCountKafka {
    private static final Logger log = LogManager.getLogger(ProductViewCount.class);

    private final KafkaRecordService kafkaRecordService;
    private final RecordProducer recordProducer;


    public ProductViewCountKafka(KafkaRecordService kafkaRecordService, RecordProducer recordProducer) {
        this.kafkaRecordService = kafkaRecordService;
        this.recordProducer = recordProducer;
    }

    public void updateProductViewCount(String productId) {
        // Fetch Product View Count and increment the count, then produce updated count to Kafka
        ProductViewCount productViewCountByProductId = kafkaRecordService.getProductViewCount(productId);
        log.info("Fetched ProductViewCount: {}", productViewCountByProductId);

        ProductViewCount updatedProductViewCount = ProductViewCount.newBuilder()
                .setCategoryId(productViewCountByProductId.getCategoryId())
                .setCount(productViewCountByProductId.getCount() + 1)
                .setProductId(productId)
                .build();
        // Produce the updated product view count back to Kafka
        recordProducer.sendProductViewCountToKafka(productId, updatedProductViewCount);
        log.info("Produced updated product view count to Kafka for productId: {}", productId);
    }
}
