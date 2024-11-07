package com.osc.sessiondataservice.config;

import com.osc.avro.Session;
import com.osc.avro.SessionKey;
import com.osc.sessiondataservice.kafka.serdes.AppSerde;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.modelmapper.ModelMapper;
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

    public static final String APPLICATION_ID = "OSC_02";
    public static final String BOOTSTRAP_SERVERS = "192.168.99.223:19092";
    public static final String SCHEMA_REGISTRY_URL = "http://192.168.99.223:18081";
    public static final String SESSION_TOPIC = "KHAN-SESSION-TOPIC";
    public static String SESSION_STORE_NAME="KHAN-SESSION-STORE";


    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean(name = "session-producer")
    public ProducerFactory<Object, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConfiguration.BOOTSTRAP_SERVERS);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        configProps.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, KafkaConfiguration.SCHEMA_REGISTRY_URL);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<Object, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean(name = "session-topic")
    public NewTopic createUserTopic() {
        return new NewTopic(KafkaConfiguration.SESSION_TOPIC, 12, (short) 1);
    }


    @Bean
    @Qualifier("streams-properties-1")
    public Properties streamsProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, KafkaConfiguration.APPLICATION_ID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConfiguration.BOOTSTRAP_SERVERS);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, KafkaConfiguration.SCHEMA_REGISTRY_URL);
        //props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);  // Required for exactly-once processing
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 15);
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 10 * 1024 * 1024L);
        return props;
    }

    @Bean(name = "streams-builder-1")
    public StreamsBuilder streamsBuilder() {
        return new StreamsBuilder();
    }

    @Bean(name = "streams-1")
    public KafkaStreams kafkaStreams (
            @Qualifier("streams-properties-1") Properties streamsProperties,
            @Qualifier("streams-builder-1") StreamsBuilder streamsBuilder){

        sessionGKTable(streamsBuilder);

        KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(), streamsProperties);
        kafkaStreams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
        return kafkaStreams;
    }

    @Bean(name = "session-GKTable")
    public GlobalKTable<SessionKey, Session> sessionGKTable(@Qualifier("streams-builder") StreamsBuilder streamsBuilder) {
        return streamsBuilder.globalTable(
                KafkaConfiguration.SESSION_TOPIC,
                Consumed.with(AppSerde.sessionKeySerdes(), AppSerde.sessionSerdes()),
                Materialized.<SessionKey, Session, KeyValueStore<Bytes, byte[]>>as(KafkaConfiguration.SESSION_STORE_NAME)
                        .withKeySerde(AppSerde.sessionKeySerdes())
                        .withValueSerde(AppSerde.sessionSerdes())
        );
    }
}
