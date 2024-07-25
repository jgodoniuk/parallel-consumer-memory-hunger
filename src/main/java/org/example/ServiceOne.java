package org.example;

import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;

public class ServiceOne {

  private ServiceTwo nestedService = new ServiceTwo();

  public List<ProducerRecord<Object, Object>> process(ConsumerRecord<Object, Object> consumerRecord) {
    try {
      nestedService.process(consumerRecord);
      return List.of();
    } catch (Exception e) {
      throw new ServiceOneException("An exception reported in service one.", e);
    }
  }
}
