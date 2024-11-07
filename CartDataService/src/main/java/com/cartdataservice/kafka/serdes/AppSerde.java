package com.cartdataservice.kafka.serdes;

import com.cartdataservice.config.ApplicationConfig;
import com.osc.avro.CartList;
import com.osc.avro.Product;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class AppSerde {


    public static SpecificAvroSerde<CartList> cartListSerdes() {
        SpecificAvroSerde<CartList> serde = new SpecificAvroSerde<>();
        Map<String, String> serdeConfig = Collections.singletonMap(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                ApplicationConfig.SCHEMA_REGISTRY_URL);
        serde.configure(serdeConfig, false);
        return serde;
    }

}


