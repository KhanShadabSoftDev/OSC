package com.osc.productdataservice.kafka.serdes;

import com.osc.avro.CategoryDetails;
import com.osc.avro.ProductDetails;
import com.osc.avro.ProductViewCount;
import com.osc.productdataservice.config.ApplicationConfig;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class AppSerde {

    public static SpecificAvroSerde<ProductDetails> productSerdes() {
        SpecificAvroSerde<ProductDetails> serde = new SpecificAvroSerde<>();
        Map<String, String> serdeConfig = Collections.singletonMap(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                ApplicationConfig.SCHEMA_REGISTRY_URL);
        serde.configure(serdeConfig, false);
        return serde;
    }

    public static SpecificAvroSerde<CategoryDetails> categorySerdes() {
        SpecificAvroSerde<CategoryDetails> serde = new SpecificAvroSerde<>();
        Map<String, String> serdeConfig = Collections.singletonMap(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                ApplicationConfig.SCHEMA_REGISTRY_URL);
        serde.configure(serdeConfig, false);
        return serde;
    }

    public static SpecificAvroSerde<ProductViewCount> productViewCountSerdes() {
        SpecificAvroSerde<ProductViewCount> serde = new SpecificAvroSerde<>();
        Map<String, String> serdeConfig = Collections.singletonMap(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                ApplicationConfig.SCHEMA_REGISTRY_URL);
        serde.configure(serdeConfig, false);
        return serde;
    }

    public static Serde<List<String>> listSerde() {
        Serde<List<String>> serde = Serdes.ListSerde(ArrayList.class, Serdes.String());

        Map<String, String> serdeConfig = Collections.singletonMap(
                AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                ApplicationConfig.SCHEMA_REGISTRY_URL
        );

        serde.configure(serdeConfig, false);  // false indicates it's a value serde

        return serde;
    }
}

