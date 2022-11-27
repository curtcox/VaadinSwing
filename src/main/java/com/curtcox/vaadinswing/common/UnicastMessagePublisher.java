package com.curtcox.vaadinswing.common;

import com.curtcox.vaadinswing.common.Message;
import reactor.core.publisher.UnicastProcessor;

public final class UnicastMessagePublisher implements Message.Publisher {

    final UnicastProcessor<Message> processor;

    public UnicastMessagePublisher(UnicastProcessor<Message> processor) {
        this.processor = processor;
    }

    @Override
    public void publish(Message message) {
        processor.onNext(message);
    }
}
