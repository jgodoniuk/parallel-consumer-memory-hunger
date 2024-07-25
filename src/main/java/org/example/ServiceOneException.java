package org.example;

public class ServiceOneException extends RuntimeException {

  public ServiceOneException(String message, Exception cause) {
    super(message, cause);
  }
}
