package chatRabbitMQ.gui.widgets;

import chatRabbitMQ.chat.ClientGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.rmi.RemoteException;

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
        this.add(new SendButtons(this, client), BorderLayout.EAST);
    }

    public void setupTextFieldEnterAction(ClientGUI client) {
        m_textFieldEnterAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    sendMessage(client);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public void sendMessage(ClientGUI client) throws RemoteException
    {
        /*if ( ! client.isLoggedIn()) {
            ContentPanel.addMessage(new Message("Connectez vous pour envoyer un message !", "Application", Message.MessageType.APPLICATION));
            return;
        }

        if (getSendBarText().isEmpty())
            return;

        Message message = new Message(getSendBarText(), client.getName());
        client.sendMessage(message);
        clearText();*/
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