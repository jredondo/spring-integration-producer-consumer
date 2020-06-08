package org.streamexperiments.cep.process;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.TypeInformationSerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import java.util.*;

import org.apache.flink.types.StringValue;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.streamexperiments.models.Update;

public class

CEP {

    public static final String KAFKA_TOPIC_PRODUCER = "producer.topic";
    public static final String KAFKA_TOPIC_CONSUMER = "consumer.topic";

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "192.168.99.147:9092");

        TypeInformation<Update> typeInfo = TypeExtractor.getForClass(Update.class);
        TypeInformationSerializationSchema<Update> schema =
                new TypeInformationSerializationSchema<Update>(typeInfo, new ExecutionConfig());


        DataStream<Update> incomingStream = env
                .addSource(new FlinkKafkaConsumer<>(KAFKA_TOPIC_PRODUCER, schema, properties));

        UpdateSerializationSchema kafkaSchema = new UpdateSerializationSchema(KAFKA_TOPIC_CONSUMER);

        FlinkKafkaProducer<Update> kafkaProducer = new FlinkKafkaProducer<>(KAFKA_TOPIC_CONSUMER,
                kafkaSchema,
                properties,
                FlinkKafkaProducer.Semantic.EXACTLY_ONCE);

        kafkaProducer.setWriteTimestampToKafka(true);

        DataStreamSink<Update> returnStream = incomingStream.addSink(kafkaProducer);

        returnStream.setParallelism(1);

        env.execute("Producer/Flink/Consumer Demonstration");

    }

    public static class UpdateSerializationSchema implements KafkaSerializationSchema<Update> {

        private String topic;
        private ObjectMapper mapper;

        public UpdateSerializationSchema(String topic) {
            super();
            this.topic = topic;
        }

        @Override
        public ProducerRecord<byte[], byte[]> serialize(Update update, Long timestamp) {
            byte[] blob = null;
            if (mapper == null) {
                mapper = new ObjectMapper();
            }
            try {
                blob = mapper.writeValueAsBytes(update);
            } catch (JsonProcessingException e) {
                System.out.println(e.getMessage());
            }
            return new ProducerRecord<>(topic, blob);
        }
    }
}
