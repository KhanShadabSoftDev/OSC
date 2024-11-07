package com.osc.recentlyviewedservice.kafka.consumer;

import com.osc.avro.RecentViewHistory;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.stereotype.Service;

@Service
public class KafkaRecordService {


    private final ReadOnlyKeyValueStore<String, RecentViewHistory> recentlyViewedStore;

    public KafkaRecordService(ReadOnlyKeyValueStore<String, RecentViewHistory> recentlyViewedStore) {
        this.recentlyViewedStore = recentlyViewedStore;
    }

//        Get 6 recently viewed products from {recently-viewed-products} Topic.
//        Fetch the recent view history from the KTable store for the given userId.

    public RecentViewHistory getRecentlyViewedProductList(String userId) {
        return recentlyViewedStore.get(userId);
    }

}



