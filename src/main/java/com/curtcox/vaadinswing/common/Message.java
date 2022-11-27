package com.curtcox.vaadinswing.common;

import java.util.function.*;

public final class Message {
  final String message;

  public Message(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public interface Publisher {
    void publish(Message message);
  }

  public interface Publication {
    void subscribe(Consumer<Message> consumer);
  }
}