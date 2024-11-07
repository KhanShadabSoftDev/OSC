package com.osc.recentlyviewedservice.kafka.serdes;

import com.osc.avro.CategoryDetails;
import com.osc.avro.RecentViewHistory;
import com.osc.recentlyviewedservice.config.ApplicationConfig;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class AppSerde {


    public static SpecificAvroSerde<RecentViewHistory> recentHistorySerdes() {
        SpecificAvroSerde<RecentViewHistory> serde = new SpecificAvroSerde<>();
        Map<String, String> serdeConfig = Collections.singletonMap(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                ApplicationConfig.SCHEMA_REGISTRY_URL);
        serde.configure(serdeConfig, false);
        return serde;
    }

}
