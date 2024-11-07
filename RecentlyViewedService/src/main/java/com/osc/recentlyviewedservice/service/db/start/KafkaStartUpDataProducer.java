package com.osc.recentlyviewedservice.service.db.start;

import com.osc.avro.RecentViewHistory;
import com.osc.recentlyviewedservice.dbservice.RecentViewedDB;
import com.osc.recentlyviewedservice.kafka.producer.RecordProducer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KafkaStartUpDataProducer implements ApplicationRunner {

    private final RecentViewedDB recentViewedDB;
    private final RecordProducer recordProducer;

    public KafkaStartUpDataProducer(RecentViewedDB recentViewedDB, RecordProducer recordProducer) {
        this.recentViewedDB = recentViewedDB;
        this.recordProducer = recordProducer;
    }



    //    On Application StartUp it will produce all the RecentlyViewed Data to kafka which is stored in DB.
    @Override
    public void run(ApplicationArguments args) throws Exception {

        Map<String, RecentViewHistory> recentViewHistories = recentViewedDB.fetchFromDB();

        for (Map.Entry<String, RecentViewHistory> entry : recentViewHistories.entrySet()) {
            String userId = entry.getKey();
            RecentViewHistory recentViewHistory = entry.getValue();

            recordProducer.sendRecentViewHistoryToRVTopic(userId, recentViewHistory);
        }
    }
}