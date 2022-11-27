package com.curtcox.vaadinswing.swing;

import com.curtcox.vaadinswing.common.Message;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public final class SwingView extends JFrame {

    final JSlider alphaSlider = new JSlider();
    final JSlider fontSlider = new JSlider();
    final JTextArea text = new JTextArea();
    final BorderLayout layout = new BorderLayout();
    final ComponentResizer resizer = new ComponentResizer();

    final Message.Publication messages;
    final Message.Publisher publisher;

    private long lastUpdate;

    private SwingView(Message.Publisher publisher, Message.Publication messages) {
        this.messages = messages;
        this.publisher = publisher;
        setUndecorated(true);
        setLayout(layout);
        setAlpha(0.5f);
        setFont(0.5f);
        setSize(500,500);
    }

    private void addComponents() {
        alphaSlider.setOpaque(false);
        fontSlider.setOpaque(false);
        text.setOpaque(false);
        add(text,BorderLayout.CENTER);
        add(alphaSlider,BorderLayout.NORTH);
        add(fontSlider,BorderLayout.WEST);
        fontSlider.setOrientation(JSlider.VERTICAL);
    }

    private void addListeners() {
        alphaSlider.addChangeListener(e -> setAlpha(value(alphaSlider)));
        fontSlider.addChangeListener(e -> setFont(value(fontSlider)));
        text.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { publish(); }
            @Override public void removeUpdate(DocumentEvent e)  { publish(); }
            @Override public void changedUpdate(DocumentEvent e) { publish(); }
        });
    }

    private void publish() {
        lastUpdate = System.currentTimeMillis();
        publisher.publish(new Message(text.getText()));
    }

    private float value(JSlider slider) {
        return ((float) slider.getValue()) / 100.0f;
    }

    private void setFont(float x) {
        float y = x + 1.0f;
        double size = (Math.pow(x + 1.0d,10.0d) + 5.0d);
        text.setFont(new Font("Courier New",Font.BOLD, (int) size));
    }

    private void setAlpha(float alpha) {
        Color color = new Color(0.0f,0.0f,0.0f,alpha);
        setBackground(color);
        text.setBackground(color);
        alphaSlider.setBackground(color);
    }
    static SwingView of(Message.Publisher publisher, Message.Publication messages) {
        SwingView frame = new SwingView(publisher,messages);
        frame.addComponents();
        frame.addListeners();
        frame.resizer.registerComponent(frame);
        frame.listen();
        frame.setLocationRelativeTo(null);
        return frame;
    }

    void listen() {
        listenToMouse();
        listenToKeyboard();
        listenToMessages();
    }

    private void listenToMessages() {
        messages.subscribe(message -> EventQueue.invokeLater(() -> on(message) ));
    }

    private void on(Message message) {
        if (longEnoughSinceLastUpdate()) {
            text.setText(message.getMessage());
        }
    }

    private boolean longEnoughSinceLastUpdate() {
        return System.currentTimeMillis() - lastUpdate > 1000;
    }

    private void listenToKeyboard() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(e -> {
                    on(e);
                    return false;
                });
    }

    private void listenToMouse() {
        long mask = AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK;
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> on(event), mask);
    }

    private void on(AWTEvent event) {
        if (event instanceof MouseEvent) {
            on((MouseEvent) event);
        }
    }

    private void on(MouseEvent mouseEvent) {
        int id = mouseEvent.getID();
        if (id==MouseEvent.MOUSE_ENTERED)  resizer.mouseEntered(mouseEvent);
        if (id==MouseEvent.MOUSE_EXITED)   resizer.mouseExited(mouseEvent);
        if (id==MouseEvent.MOUSE_MOVED)    resizer.mouseMoved(mouseEvent);
        if (id==MouseEvent.MOUSE_PRESSED)  resizer.mousePressed(mouseEvent);
        if (id==MouseEvent.MOUSE_RELEASED) resizer.mouseReleased(mouseEvent);
        if (id==MouseEvent.MOUSE_CLICKED)  resizer.mouseClicked(mouseEvent);
        if (id==MouseEvent.MOUSE_DRAGGED)  resizer.mouseDragged(mouseEvent);
    }

    private void on(KeyEvent keyEvent) {
        //System.out.println("Got key event! " + keyEvent);
    }

    public static void showFrame(Message.Publisher publisher, Message.Publication messages) {
        SwingUtilities.invokeLater(() -> {
            SwingView frame = SwingView.of(publisher,messages);
            frame.setOpacity(0.5f);
            frame.setVisible(true);
        });
    }
}


