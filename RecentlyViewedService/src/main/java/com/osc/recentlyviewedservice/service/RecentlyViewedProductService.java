package com.osc.recentlyviewedservice.service;


import com.grpc.recentHistory.RecentlyViewedResponse;
import com.osc.avro.RecentViewHistory;
import com.osc.recentlyviewedservice.dbservice.RecentViewedDB;
import com.osc.recentlyviewedservice.kafka.consumer.KafkaRecordService;
import com.osc.recentlyviewedservice.kafka.producer.RecordProducer;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecentlyViewedProductService {

    private static final Logger log = LogManager.getLogger(RecentlyViewedProductService.class);

    private final RecordProducer recordProducer;
    private final KafkaRecordService kafkaRecordService;
    private final RecentViewedDB recentViewedDB;

    public RecentlyViewedProductService(RecordProducer recordProducer, ReadOnlyKeyValueStore<String, RecentViewHistory> recentlyViewedStore, KafkaRecordService kafkaRecordService, RecentViewedDB recentViewedDB) {
        this.recordProducer = recordProducer;
        this.kafkaRecordService = kafkaRecordService;
        this.recentViewedDB = recentViewedDB;
    }

    // Method to fetch recently viewed products for a user and prepare a response
    public RecentlyViewedResponse fetchRecentlyViewedProducts(String userId) {
        // Retrieve recently viewed products for the given userId
        RecentViewHistory products = kafkaRecordService.getRecentlyViewedProductList(userId);
        log.debug("Fetched recently viewed products for userId: {}. Product details: {}", userId, products);

        List<String> productIdList = new ArrayList<>();

        if (products != null && products.getProductIds() != null) {
            for (CharSequence productId : products.getProductIds()) {
                productIdList.add(productId.toString());
            }

            log.info("User Has Recently Viewed Products: {}", productIdList);
            log.debug("User {} has {} Recently Viewed Products.", userId, productIdList.size());
        } else {
            log.info("No recently viewed products found for userId: {}", userId);
        }

        // Build the response with the product IDs (empty list if none found)
        return RecentlyViewedResponse.newBuilder()
                .addAllProductId(productIdList)
                .build();
    }

    // Produce a message to update the recently viewed product list for a user
    public void produceRecentlyViewedProduct(String userId, String newProductId) {
        recordProducer.sendRecentViewHistoryToPVTopic(userId, newProductId);
    }

    // Sync recently viewed products from Kafka to the database
    public void syncRecentlyViewedToDB(String userId) {

        RecentViewHistory recentViewHistory = kafkaRecordService.getRecentlyViewedProductList(userId);

        List<String> productIdsList = new ArrayList<>();
        if (recentViewHistory != null) {
            for (CharSequence productId : recentViewHistory.getProductIds()) {
                productIdsList.add(productId.toString());
            }
            recentViewedDB.saveRVListInDB(userId, productIdsList);
        }
    }

}
