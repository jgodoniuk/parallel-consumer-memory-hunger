package org.example;

public class ServiceTwoException extends RuntimeException {

  public ServiceTwoException(String message, Exception cause) {
    super(message, cause);
  }
}
