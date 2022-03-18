package chatRabbitMQ.application.gui.widgets;

import javax.swing.*;
import java.awt.*;

public class SendButtons extends JPanel {
    public SendButtons(SendMessageBar sendMessageBar) {

        this.setLayout(new BorderLayout());

        JButton sendButton = new JButton("Envoyer");
        this.add(sendButton, BorderLayout.EAST);
        sendButton.addActionListener(sendMessageBar.getTextFieldEnterAction());
    }
}