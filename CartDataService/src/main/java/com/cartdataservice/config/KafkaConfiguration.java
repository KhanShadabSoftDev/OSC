package com.cartdataservice.config;

import com.cartdataservice.kafka.serdes.AppSerde;
import com.osc.avro.CartList;
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


    @Bean(name = "Cart-topic")
    public NewTopic cartTopic() {
        return new NewTopic(ApplicationConfig.CART_TOPIC, 12, (short) 1);
    }


    @Bean(name = "producer")
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, ApplicationConfig.BOOTSTRAP_SERVER);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // Change this if you switch to producing Avro objects
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

    @Bean(name = "kafka-streams")
    public KafkaStreams kafkaStreams(
            @Qualifier("product-streams-properties") Properties streamsProperties,
            @Qualifier("streams-builder") StreamsBuilder streamsBuilder) {

        cartKTable(streamsBuilder);

        KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(), streamsProperties);
        kafkaStreams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
        return kafkaStreams;
    }

    @Bean(name = "cart-KTable")
    public KTable<String, CartList> cartKTable(@Qualifier("streams-builder") StreamsBuilder streamsBuilder) {

        return streamsBuilder.table(
                ApplicationConfig.CART_TOPIC,
                Consumed.with(Serdes.String(), AppSerde.cartListSerdes()),
                Materialized.<String, CartList, KeyValueStore<Bytes, byte[]>>as(ApplicationConfig.CART_STORE)
                        .withKeySerde(Serdes.String())
                        .withValueSerde(AppSerde.cartListSerdes())
        );
    }

    @Bean
    public ReadOnlyKeyValueStore<String, CartList> cartStore(
            @Qualifier("kafka-streams") KafkaStreams kafkaStreams) throws InterruptedException {

        // Wait for the KafkaStreams to be ready
        while (kafkaStreams.state() != KafkaStreams.State.RUNNING) {
            Thread.sleep(100);
        }
        return kafkaStreams.store(
                StoreQueryParameters.fromNameAndType(
                        ApplicationConfig.CART_STORE, QueryableStoreTypes.keyValueStore()
                )
        );
    }

}





