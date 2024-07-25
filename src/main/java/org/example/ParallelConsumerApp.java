package org.example;

import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

import io.confluent.parallelconsumer.ParallelConsumerOptions;
import io.confluent.parallelconsumer.ParallelConsumerOptions.ProcessingOrder;
import io.confluent.parallelconsumer.ParallelEoSStreamProcessor;
import io.confluent.parallelconsumer.PollContext;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

public class ParallelConsumerApp {

  private static final String KAFKA_HOST = "localhost:9092";
  private static final String ACKS = "all";
  private static final boolean ENABLE_IDEMPOTENCE = true;
  private static final int LINGER_MS = 50;
  private static final int BATCH_SIZE_BYTES = 32768;
  private static final String KAFKA_TEST_GID = "kafka-test-gid";
  private static final Duration ONE_SECOND = Duration.ofSeconds(1);
  public static final String TOPIC_NAME = "kafka-test-topic";
  public static final int MAX_CONCURRENCY = 75;

  public static void main(String[] args) throws InterruptedException {

    ParallelConsumerOptions<Object, Object> options = ParallelConsumerOptions.builder()
        .ordering(ProcessingOrder.UNORDERED)
        .consumer(new KafkaConsumer<>(
            consumerConfiguration(KAFKA_HOST, KAFKA_TEST_GID)))
        .producer(new KafkaProducer<>(
            producerConfiguration(KAFKA_HOST, LINGER_MS, BATCH_SIZE_BYTES)))
        .retryDelayProvider(objectObjectRecordContext -> ONE_SECOND)
        .commitInterval(ONE_SECOND)
        .maxConcurrency(MAX_CONCURRENCY)
        .build();

    try (var parallelStream = new ParallelEoSStreamProcessor<>(options)) {
      parallelStream.subscribe(Collections.singleton(TOPIC_NAME));
      parallelStream.pollAndProduceMany(exceptionThrowingUserFunction());
      System.out.println("Consumer started");

      Thread.sleep(999999999);
    }
  }

  private static Map<String, Object> consumerConfiguration(
      String kafkaUrl, String consumerGroupId) {
    return Map.of(
        BOOTSTRAP_SERVERS_CONFIG, kafkaUrl,
        GROUP_ID_CONFIG, consumerGroupId,
        KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
        VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class,
        ENABLE_AUTO_COMMIT_CONFIG, "false",
        AUTO_OFFSET_RESET_CONFIG, "latest");
  }

  private static Map<String, Object> producerConfiguration(
      String kafkaUrl, int lingerMs, int batchSizeBytes) {
    return Map.of(
        BOOTSTRAP_SERVERS_CONFIG, kafkaUrl,
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class,
        ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, ENABLE_IDEMPOTENCE,
        ProducerConfig.ACKS_CONFIG, ACKS,
        ProducerConfig.LINGER_MS_CONFIG, lingerMs,
        ProducerConfig.BATCH_SIZE_CONFIG, batchSizeBytes);
  }

  private static Function<PollContext<Object, Object>, List<ProducerRecord<Object, Object>>> exceptionThrowingUserFunction() {
    ServiceOne service = new ServiceOne();

    return recordContexts -> {
      var consumerRecord = recordContexts.getSingleRecord().getConsumerRecord();

      System.out.printf("USER FUNCTION: thread: %s, message key: %s\n",
          Thread.currentThread(), consumerRecord.key());

      return service.process(consumerRecord);
    };
  }
}