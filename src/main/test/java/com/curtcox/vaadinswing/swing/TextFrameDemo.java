package com.curtcox.vaadinswing.swing;

import com.curtcox.vaadinswing.common.Message;

public class TextFrameDemo {

    public static void main(String[] args) {
        SwingView.showFrame(
                message -> System.out.println(message.getMessage()),
                consumer -> consumer.accept(new Message("Start with this..."))
        );
    }
}
