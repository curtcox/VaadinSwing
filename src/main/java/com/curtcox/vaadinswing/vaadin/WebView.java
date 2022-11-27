package com.curtcox.vaadinswing.vaadin;

import com.curtcox.vaadinswing.common.Message;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

@Route("")
@StyleSheet("frontend://styles/styles.css")
@Push
@PWA(name = "WebText", shortName = "WebText")
public final class WebView extends TextArea {

  private final Message.Publisher publisher;
  private final Message.Publication messages;

  private long lastUpdate;

  public WebView(Message.Publisher publisher, Message.Publication messages) {
    this.publisher = publisher;
    this.messages = messages;
    addClassName("main-view");
    setSizeFull();
    listenToMessages();
    addValueChangeListener((ValueChangeListener) valueChangeEvent -> publish() );
  }

  private void listenToMessages() {
    messages.subscribe(message -> getUI().ifPresent(ui -> ui.access(() -> onMessage(message))));
  }

  private void onMessage(Message message) {
      if (longEnoughSinceLastUpdate()) {
          setValue(message.getMessage());
      }
  }

  private boolean longEnoughSinceLastUpdate() {
    return System.currentTimeMillis() - lastUpdate > 1000;
  }

  private void publish() {
      lastUpdate = System.currentTimeMillis();
      publisher.publish(new Message(getValue()));
  }

}
