package com.osc.sessiondataservice.kafka.serdes;

import com.osc.avro.Session;
import com.osc.avro.SessionKey;
import com.osc.sessiondataservice.config.KafkaConfiguration;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class AppSerde {

    public static SpecificAvroSerde<Session> sessionSerdes() {
        SpecificAvroSerde<Session> serde = new SpecificAvroSerde<>();
        Map<String, String> serdeConfig = Collections.singletonMap(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                KafkaConfiguration.SCHEMA_REGISTRY_URL);
        serde.configure(serdeConfig, false);
        return serde;
    }
    public static SpecificAvroSerde<SessionKey> sessionKeySerdes() {
        SpecificAvroSerde<SessionKey> serde = new SpecificAvroSerde<>();
        Map<String, String> serdeConfig = Collections.singletonMap(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                KafkaConfiguration.SCHEMA_REGISTRY_URL);
        serde.configure(serdeConfig, true);
        return serde;
    }

}
