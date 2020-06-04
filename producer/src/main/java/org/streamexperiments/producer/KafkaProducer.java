package org.streamexperiments.producer;

import org.springframework.beans.factory.annotation.Value;
import org.streamexperiments.config.kafka.KafkaAppProperties;
import org.streamexperiments.producer.integration.Sender;
import org.streamexperiments.producer.integration.kafka.KafkaProducerConfiguration;
import org.streamexperiments.producer.logic.TestProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Example Kafka producer using decoupled integration ({@link KafkaProducerConfiguration.Gateway})
 * and logic ({@link TestProducer}).
 */
@Component
@ConditionalOnProperty(value="kafka.enabled", havingValue = "true")
public class KafkaProducer {


    @Value("${data-monitor.throughput}")
    private long throughput;

    @Value("${data-monitor.N}")
    private long N;

    @Autowired
    private KafkaProducerConfiguration.Gateway kafkaGateway;

    @Autowired
    private KafkaAppProperties properties;

    private TestProducer producer;

    /**
     * Using method annotated with @PostConstruct (instead of constructor)
     * to give chance to used spring beans to get in place.
     */
    @PostConstruct
    private void init() {
        producer = new TestProducer(throughput, N);
        producer.addGateway(new Sender(kafkaGateway, properties.getTopic()));
        producer.start();
    }

}
