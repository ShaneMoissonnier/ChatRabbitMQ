package chatRabbitMQ.application.gui.widgets;

import chatRabbitMQ.application.ClientGUI;

import javax.swing.*;
import java.awt.*;

public class SendButtons extends JPanel {
    private final SendMessageBar m_sendMessageBar;
    private final ClientGUI m_client;

    public SendButtons(SendMessageBar sendMessageBar, ClientGUI client) {
        this.m_sendMessageBar = sendMessageBar;
        this.m_client = client;

        this.setLayout(new BorderLayout());

        JButton sendButton = new JButton("Envoyer");
        this.add(sendButton, BorderLayout.EAST);
        sendButton.addActionListener(m_sendMessageBar.getTextFieldEnterAction());
    }
}