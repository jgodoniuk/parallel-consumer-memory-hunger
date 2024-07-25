package org.example;

public class ServiceThreeException extends RuntimeException {

  public ServiceThreeException(String message, Exception cause) {
    super(message, cause);
  }
}
