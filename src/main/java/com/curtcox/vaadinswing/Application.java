package com.curtcox.vaadinswing;

import com.curtcox.vaadinswing.common.*;
import com.curtcox.vaadinswing.swing.SwingView;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        lauchWebApp();
    }

    private static void lauchWebApp() {
        new SpringApplicationBuilder(Application.class)
                .headless(false)
                .run();
    }

    @Bean String showFrame(UnicastProcessor<Message> publisher, Flux<Message> messages) {
        SwingView.showFrame(new UnicastMessagePublisher(publisher),new FluxMessagePublication(messages));
        return "";
    }

    @Bean Flux<Message> messages(UnicastProcessor<Message> publisher) {
        return publisher.replay(1).autoConnect();
    }

    @Bean UnicastProcessor<Message> publisher() {
        return UnicastProcessor.create();
    }

    @Bean Message.Publisher messagePublisher(UnicastProcessor<Message> publisher) {
        return new UnicastMessagePublisher(publisher);
    }

    @Bean Message.Publication messagePublication(Flux<Message> messages) {
        return new FluxMessagePublication(messages);
    }
}
