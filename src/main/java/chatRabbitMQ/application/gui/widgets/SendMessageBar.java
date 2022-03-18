package chatRabbitMQ.application.gui.widgets;

import chatRabbitMQ.application.ClientGUI;
import chatRabbitMQ.messages.ChatMessage;
import chatRabbitMQ.messages.ChatMessageType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class SendMessageBar extends JPanel{
    private final JTextField m_sendBar;
    private Action m_textFieldEnterAction;

    public SendMessageBar(ClientGUI client) {
        setupTextFieldEnterAction(client);

        this.setLayout(new BorderLayout());

        this.m_sendBar = new JTextField();
        this.m_sendBar.putClientProperty("JTextField.placeholderText", " Votre message ici...");
        this.m_sendBar.setFont(new Font(m_sendBar.getFont().getName(), Font.PLAIN, 20));
        this.m_sendBar.addActionListener(m_textFieldEnterAction);

        this.setPreferredSize(new Dimension(getWidth(), 60));
        this.add(m_sendBar, BorderLayout.CENTER);
        this.add(new SendButtons(this), BorderLayout.EAST);
    }

    public void setupTextFieldEnterAction(ClientGUI client) {
        m_textFieldEnterAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    sendMessage(client);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public void sendMessage(ClientGUI client) throws IOException {
        if ( ! client.isLoggedIn()) {
            ContentPanel.addMessage(new ChatMessage(null,"Application", "Connectez vous pour envoyer un message !", ChatMessageType.APPLICATION));
            return;
        }
        String message = getSendBarText();

        if (message.isEmpty())
            return;

        client.sendMessage(message);
        clearText();
    }

    public Action getTextFieldEnterAction() {
        return m_textFieldEnterAction;
    }

    public String getSendBarText() {
        return this.m_sendBar.getText();
    }

    public void clearText() {
        this.m_sendBar.setText("");
    }
}