package com.osc.productdataservice.config;

import com.osc.avro.CategoryDetails;
import com.osc.avro.ProductDetails;
import com.osc.avro.ProductViewCount;
import com.osc.productdataservice.kafka.serdes.AppSerde;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class KafkaConfiguration {

    @Bean(name = "Product-topic")
    public NewTopic createProductTopic() {
        return new NewTopic(ApplicationConfig.PRODUCT_DETAILS_TOPIC, 12, (short) 1);
    }
    @Bean(name = "Category-topic")
    public NewTopic createCategoryTopic() {
        return new NewTopic(ApplicationConfig.CATEGORIES_DETAILS_TOPIC, 12, (short) 1);
    }
    @Bean(name = "Product-view-count-topic")
    public NewTopic createProductViewCountTopic() {
        return new NewTopic(ApplicationConfig.PRODUCT_VIEW_COUNT_TOPIC, 12, (short) 1);
    }

    @Bean(name = "producer")
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, ApplicationConfig.BOOTSTRAP_SERVER);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        configProps.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, ApplicationConfig.SCHEMA_REGISTRY_URL);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }


    @Bean
    @Qualifier("product-streams-properties")
    public Properties streamsProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, ApplicationConfig.APPLICATION_ID_CONFIG);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, ApplicationConfig.BOOTSTRAP_SERVER);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, ApplicationConfig.SCHEMA_REGISTRY_URL);
        // Uncomment for exactly-once processing
        // props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 15);
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 100L);
        return props;
    }

    @Bean(name = "streams-builder")
    public StreamsBuilder streamsBuilder() {
        return new StreamsBuilder();
    }

    @Bean(name = "kafka-streams-pds")
    public KafkaStreams kafkaStreams(
            @Qualifier("product-streams-properties") Properties streamsProperties,
            @Qualifier("streams-builder") StreamsBuilder streamsBuilder) {

        productDetailsKTable(streamsBuilder);
        categoryDetailsKTable(streamsBuilder);
        productViewCountKTable(streamsBuilder);

        KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(), streamsProperties);
        kafkaStreams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
        return kafkaStreams;
    }

    @Bean
    public ReadOnlyKeyValueStore<String, CategoryDetails> categoryDetailsStore(
            @Qualifier("kafka-streams-pds") KafkaStreams kafkaStreams) throws InterruptedException {

        // Wait for the KafkaStreams to be ready
        while (kafkaStreams.state() != KafkaStreams.State.RUNNING) {
            Thread.sleep(100);
        }
        return kafkaStreams.store(
                StoreQueryParameters.fromNameAndType(
                        ApplicationConfig.CATEGORY_STORE_NAME, QueryableStoreTypes.keyValueStore()
                )
        );
    }
    @Bean
    public ReadOnlyKeyValueStore<String, ProductViewCount> productViewCountStore(
            @Qualifier("kafka-streams-pds") KafkaStreams kafkaStreams) throws InterruptedException {

        // Wait for the KafkaStreams to be ready
        while (kafkaStreams.state() != KafkaStreams.State.RUNNING) {
            Thread.sleep(100);
        }
        return kafkaStreams.store(
                StoreQueryParameters.fromNameAndType(
                        ApplicationConfig.PRODUCT_VIEW_STORE_NAME, QueryableStoreTypes.keyValueStore()
                )
        );
    }



    // Product View Count KTable

    @Bean(name = "product-view-count-KTable")
    public KTable<String, ProductViewCount> productViewCountKTable(
            @Qualifier("streams-builder") StreamsBuilder streamsBuilder) {

        return streamsBuilder.table(
                ApplicationConfig.PRODUCT_VIEW_COUNT_TOPIC,
                Consumed.with(Serdes.String(), AppSerde.productViewCountSerdes()),
                Materialized.<String, ProductViewCount, KeyValueStore<Bytes, byte[]>>as(ApplicationConfig.PRODUCT_VIEW_STORE_NAME)
                        .withKeySerde(Serdes.String())
                        .withValueSerde(AppSerde.productViewCountSerdes())
        );
    }

    @Bean
    public ReadOnlyKeyValueStore<String, ProductDetails> productStore(
            @Qualifier("kafka-streams-pds") KafkaStreams kafkaStreams) throws InterruptedException {

        // Wait for the KafkaStreams to be ready
        while (kafkaStreams.state() != KafkaStreams.State.RUNNING) {
            Thread.sleep(100);
        }
        return kafkaStreams.store(
                StoreQueryParameters.fromNameAndType(
                        ApplicationConfig.PRODUCT_STORE_NAME, QueryableStoreTypes.keyValueStore()
                )
        );
    }



    @Bean(name = "product-details-KTable")
    public GlobalKTable<String, ProductDetails> productDetailsKTable(@Qualifier("streams-builder") StreamsBuilder streamsBuilder) {
        return streamsBuilder.globalTable(
                ApplicationConfig.PRODUCT_DETAILS_TOPIC,
                Consumed.with(Serdes.String(), AppSerde.productSerdes()),
                Materialized.<String, ProductDetails, KeyValueStore<Bytes, byte[]>>as(ApplicationConfig.PRODUCT_STORE_NAME)
                        .withKeySerde(Serdes.String())
                        .withValueSerde(AppSerde.productSerdes())
        );
    }


    @Bean(name = "category-details-KTable")
    public GlobalKTable<String, CategoryDetails> categoryDetailsKTable(@Qualifier("streams-builder") StreamsBuilder streamsBuilder) {
        return streamsBuilder.globalTable(
                ApplicationConfig.CATEGORIES_DETAILS_TOPIC,
                Consumed.with(Serdes.String(), AppSerde.categorySerdes()),
                Materialized.<String, CategoryDetails, KeyValueStore<Bytes, byte[]>>as(ApplicationConfig.CATEGORY_STORE_NAME)
                        .withKeySerde(Serdes.String())
                        .withValueSerde(AppSerde.categorySerdes())
        );
    }

}





