package org.example;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class ServiceThree {

  private ServiceFour nestedService = new ServiceFour();

  public void process(ConsumerRecord<Object, Object> consumerRecord) {

    try {
      nestedService.process(consumerRecord);
    } catch (Exception e) {
      throw new ServiceThreeException("Exception occured in service three", e);
    }
  }
}
