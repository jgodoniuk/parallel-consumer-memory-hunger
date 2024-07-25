package org.example;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class ServiceTwo {

  ServiceThree nestedService = new ServiceThree();

  public void process(ConsumerRecord<Object, Object> consumerRecord) {
    try {
      nestedService.process(consumerRecord);
    } catch (Exception e) {
      throw new ServiceTwoException("An exception reported in service two.", e);
    }
  }
}
