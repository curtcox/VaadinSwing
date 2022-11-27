package com.curtcox.vaadinswing.common;

import com.curtcox.vaadinswing.common.Message;
import reactor.core.publisher.Flux;

import java.util.function.*;

public class FluxMessagePublication implements Message.Publication {

    final Flux<Message> messages;

    public FluxMessagePublication(Flux<Message> messages) {
        this.messages = messages;
    }

    @Override
    public void subscribe(Consumer<Message> consumer) {
        messages.subscribe(consumer);
    }
}
