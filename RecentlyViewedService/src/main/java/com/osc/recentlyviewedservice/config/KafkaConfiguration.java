package com.osc.recentlyviewedservice.config;

import com.osc.avro.RecentViewHistory;
import com.osc.recentlyviewedservice.kafka.serdes.AppSerde;
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
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.*;

@Configuration
public class KafkaConfiguration {

    @Bean(name = "Recently-Viewed-Product-topic")
    public NewTopic recentlyViewedProductTopic() {
        return new NewTopic(ApplicationConfig.RECENTLY_VIEWED_PRODUCT, 12, (short) 1);
    }

    @Bean(name = "Product-View-topic")
    public NewTopic productViewTopic() {
        return new NewTopic(ApplicationConfig.PRODUCT_VIEW_TOPIC, 12, (short) 1);
    }

// This producer is used for producing to product view topic.
// There is no need to mention a producer for recentlyviewed topic because it producind from the stream itself ?
    @Bean(name = "producer")
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, ApplicationConfig.BOOTSTRAP_SERVER);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // Change this if you switch to producing Avro objects
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, ApplicationConfig.SCHEMA_REGISTRY_URL);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

//    This producer will be used to produce data to RV Topic from DB when the user LogsOut.
    @Bean(name = "from-DB-Producer")
    public ProducerFactory<String, Object> producerFactoryToRVTopic() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, ApplicationConfig.BOOTSTRAP_SERVER);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // Change this if you switch to producing Avro objects
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        configProps.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, ApplicationConfig.SCHEMA_REGISTRY_URL);
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // KafkaTemplate for Avro objects
    @Bean(name = "objectKafkaTemplate")
    public KafkaTemplate<String, Object> objectKafkaTemplate() {
        return new KafkaTemplate<>(producerFactoryToRVTopic());
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

    @Bean(name = "kafka-streams")
    public KafkaStreams kafkaStreams(
            @Qualifier("product-streams-properties") Properties streamsProperties,
            @Qualifier("streams-builder") StreamsBuilder streamsBuilder) {

        recentlyViewedKTable(streamsBuilder);
        productViewStreams(streamsBuilder);

        KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(), streamsProperties);
        kafkaStreams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
        return kafkaStreams;
    }

    @Bean
    public ReadOnlyKeyValueStore<String, RecentViewHistory> recentViewedProductStore(
            @Qualifier("kafka-streams") KafkaStreams kafkaStreams) throws InterruptedException {

        // Wait for the KafkaStreams to be ready
        while (kafkaStreams.state() != KafkaStreams.State.RUNNING) {
            Thread.sleep(100);
        }
        return kafkaStreams.store(
                StoreQueryParameters.fromNameAndType(
                        ApplicationConfig.RECENTLY_VIEWED_PRODUCT_STORE_NAME, QueryableStoreTypes.keyValueStore()
                )
        );
    }

    @Bean(name = "product-view-stream")
    public KStream<String, String> productViewStreams(@Qualifier("streams-builder") StreamsBuilder streamsBuilder) {
        // Stream data from the product-view-topic
        KStream<String, String> productViewStream = streamsBuilder.stream(
                ApplicationConfig.PRODUCT_VIEW_TOPIC,
                Consumed.with(Serdes.String(), Serdes.String()) // Both key (userId) and value (productId) are strings
        );

        // Group by userId (key) and aggregate the product views per user
        productViewStream
                .groupByKey(Grouped.with(Serdes.String(), Serdes.String())) // Grouping by userId
                .aggregate(
                        // Initialize with an empty RecentViewHistory object
                        () -> RecentViewHistory.newBuilder().setProductIds(new ArrayList<>()).build(),
                        // Aggregation logic: update the list of product IDs for the user
                        (userId, newProductId, currentHistory) -> updateProductList(currentHistory, newProductId),
                        Materialized.with(Serdes.String(), AppSerde.recentHistorySerdes()) // Materialize the store with String keys and Avro values
                )
                .toStream() // Convert KTable back to KStream
                .to(ApplicationConfig.RECENTLY_VIEWED_PRODUCT, Produced.with(Serdes.String(), AppSerde.recentHistorySerdes())); // Write to Kafka

        return productViewStream;
    }

    @Bean(name = "RecentlyViewed-products-KTable")
    public KTable<String, RecentViewHistory> recentlyViewedKTable(@Qualifier("streams-builder") StreamsBuilder streamsBuilder) {
        KTable<String, RecentViewHistory> recentlyViewedProducts = streamsBuilder.table(
                ApplicationConfig.RECENTLY_VIEWED_PRODUCT,
                Consumed.with(Serdes.String(), AppSerde.recentHistorySerdes()),
                Materialized.<String, RecentViewHistory, KeyValueStore<Bytes, byte[]>>as(ApplicationConfig.RECENTLY_VIEWED_PRODUCT_STORE_NAME)
                        .withKeySerde(Serdes.String())
                        .withValueSerde(AppSerde.recentHistorySerdes())
        );
        // The KTable will automatically maintain state, no need for additional mapValues here
        return recentlyViewedProducts;
    }

//  Helper Method to update the productList used by productViewStreams method.
    private RecentViewHistory updateProductList(RecentViewHistory currentHistory, String newProductId) {
        // If the current history is null or doesn't have productIds, initialize an empty list
        List<CharSequence> productIds = currentHistory != null && currentHistory.getProductIds() != null
                ? new ArrayList<>(currentHistory.getProductIds())
                : new ArrayList<>();


        // Iterating through the list and remove any entry that matches newProductId (as String)
        productIds.removeIf(productId -> productId.toString().equals(newProductId));

        // Add the newProductId at the beginning of the list
        productIds.add(0, newProductId);
        // Ensure the list does not exceed 6 items
        if (productIds.size() > 6) {
            productIds = productIds.subList(0, 6);
        }

        // Return a new RecentViewHistory object with the updated product list
        return RecentViewHistory.newBuilder()
                .setProductIds(productIds)  // Avro expects a List of CharSequence
                .build();
    }

}





