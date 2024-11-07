package com.osc.productdataservice.dbservice;
import com.osc.productdataservice.kafka.consumer.KafkaRecordService;
import com.osc.productdataservice.repository.ProductViewCountRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProductViewCountDB {

    private static final Logger log = LogManager.getLogger(ProductViewCountDB.class);

    private final KafkaRecordService kafkaRecordService;
    private final ProductViewCountRepository productViewCountRepository;

    public ProductViewCountDB(KafkaRecordService kafkaRecordService, ProductViewCountRepository productViewCountRepository) {
        this.kafkaRecordService = kafkaRecordService;
        this.productViewCountRepository = productViewCountRepository;
    }

    // 60 seconds in milliseconds
    @Scheduled(fixedRate = 60000)
    public void updateProductViewCountInDB() {
        // Fetch the product view count objects
        Map<String, Long> productViewCounts = kafkaRecordService.getProductViewCounts();
        productViewCounts.forEach(productViewCountRepository::updateViewCountByProductId);
        log.info("Successfully updated product view counts in the database.");
    }
}



