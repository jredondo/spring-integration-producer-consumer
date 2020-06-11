package org.streamexperiments.cep.process;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.serialization.TypeInformationSerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.org.yaml.snakeyaml.Yaml;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.api.windowing.time.Time;


import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.*;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.streamexperiments.cep.config.Configuration;
import org.streamexperiments.models.Update;

public class CEP {

    private static Logger logger = LogManager.getLogger(CEP.class);
    private static final String CONFIG = "application.yaml";

    public static void main(String[] args) throws Exception {
        final Configuration.Kafka config = loadConfiguration().getKafka();
        final String KAFKA_TOPIC_PRODUCER = config.getProducerTopic();
        final String KAFKA_TOPIC_CONSUMER = config.getConsumerTopic();
        final Properties properties = loadProperties(config);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        UpdateDeserializationSchema deserializationSchema = new UpdateDeserializationSchema();
        UpdateSerializationSchema serializationSchema = new UpdateSerializationSchema(KAFKA_TOPIC_CONSUMER);

        DataStream<Update> incomingStream = env
                .addSource(new FlinkKafkaConsumer<Update>(KAFKA_TOPIC_PRODUCER, deserializationSchema, properties))
                .setParallelism(1)
                .flatMap(
                        new LogFlatMapFunction())
                .setParallelism(1);

        DataStream<Collection<Update>> windowedStream15secs = incomingStream
                .keyBy((KeySelector<Update, String>) Update::getSender)
                .timeWindow(Time.seconds(15), Time.seconds(5))
                // Tumbling windows for testing
                //.timeWindow(Time.seconds(15))
                .apply(new WindowedUpdatesFunction());


        FlinkKafkaProducer<Update> kafkaProducer = buildKafkaProducer(KAFKA_TOPIC_CONSUMER, properties, serializationSchema);

        DataStreamSink<Update> returnStream = incomingStream.addSink(kafkaProducer);

        returnStream.setParallelism(1);

        env.execute("Producer/Flink/Consumer Demonstration");
    }

    private static FlinkKafkaProducer<Update> buildKafkaProducer(String KAFKA_TOPIC_CONSUMER, Properties properties, UpdateSerializationSchema serializationSchema) {
        FlinkKafkaProducer<Update> kafkaProducer = new FlinkKafkaProducer<>(KAFKA_TOPIC_CONSUMER,
                serializationSchema,
                properties,
                FlinkKafkaProducer.Semantic.NONE);
        kafkaProducer.setWriteTimestampToKafka(true);
        return kafkaProducer;
    }

    private static Properties loadProperties(Configuration.Kafka config) {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", config.getBootstrapServers());
        properties.setProperty("zookeeper.connect", config.getZookeeperConnect());
        properties.setProperty("group.id", config.getGroupId());
        return properties;
    }

    private static Configuration loadConfiguration() {
        Yaml yaml = new Yaml();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream in = classLoader.getResourceAsStream(CONFIG);
        return yaml.loadAs(in, Configuration.class);
    }

    public static class WindowedUpdatesFunction implements WindowFunction<Update, Collection<Update>, String, TimeWindow> {

        @Override
        public void apply(String key, TimeWindow window, Iterable<Update> updates, Collector<Collection<Update>> out) {
            Set<Update> set = new HashSet<>();

            for(Update update: updates) {
                set.add(update);
            }

            logger.info("Window processing " + set.size() + " updates.");
        }  
    }

    public static class LogFlatMapFunction implements FlatMapFunction<Update, Update> {
        @Override
        public void flatMap(Update update, Collector<Update> out) {
            logger.info("TESTING flatMap: " + update.toString());
            out.collect(update);
        }
    }

    public static class UpdateDeserializationSchema implements KafkaDeserializationSchema<Update> {
        private ObjectMapper mapper = new ObjectMapper();

        @Override
        public boolean isEndOfStream(Update nextElement) {
            return false;
        }

        @Override
        public Update deserialize(ConsumerRecord<byte[], byte[]> record) throws Exception {
            return mapper.readValue(record.value(), Update.class);
        }


        @Override
        public TypeInformation<Update> getProducedType() {
            return TypeInformation.of(new TypeHint<Update>() {
            });
        }
    }

    public static class UpdateSerializationSchema implements KafkaSerializationSchema<Update> {
        private String topic;
        private ObjectMapper mapper = new ObjectMapper();

        public UpdateSerializationSchema(String topic) {
            super();
            this.topic = topic;
        }

        @Override
        public ProducerRecord<byte[], byte[]> serialize(Update update, Long timestamp) {
            byte[] blob = null;

            try {
                blob = mapper.writeValueAsBytes(update);
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage());
            }
            return new ProducerRecord<>(topic, blob);
        }
    }
}
