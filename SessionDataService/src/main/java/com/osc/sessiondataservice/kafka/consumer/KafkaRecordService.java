package com.osc.sessiondataservice.kafka.consumer;

import com.osc.avro.Session;
import com.osc.avro.SessionKey;
import com.osc.sessiondataservice.config.KafkaConfiguration;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.stereotype.Service;

@Service
public class KafkaRecordService {

    private final KafkaStreams kafkaStreams;
//    private final RecordProducer producer;

    public KafkaRecordService(KafkaStreams kafkaStreams) {
        this.kafkaStreams = kafkaStreams;
//        this.producer = producer;
    }

    public Session getSessionBySessionId(SessionKey key) {

        ReadOnlyKeyValueStore<SessionKey, Session> userStore = kafkaStreams.store(
                StoreQueryParameters.fromNameAndType(KafkaConfiguration.SESSION_STORE_NAME, QueryableStoreTypes.keyValueStore())
        );

        return userStore.get(key);
    }


}
