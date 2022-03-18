package chatRabbitMQ.application.gui.widgets;

import chatRabbitMQ.application.ClientGUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConnectionButtons extends JPanel implements ActionListener {
    private final ClientGUI client;

    private static JButton m_connectButton;
    private static JButton m_disconnectButton;

    private void createButtons() {
        m_connectButton = new JButton("Connexion");
        m_disconnectButton = new JButton("Déconnexion");

        this.add(m_connectButton, BorderLayout.LINE_START);
        this.add(m_disconnectButton, BorderLayout.LINE_END);

        m_connectButton.addActionListener(this);
        m_disconnectButton.addActionListener(this);

        m_disconnectButton.setEnabled(false);
    }

    private void setupPanel() {
        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));

        this.setPreferredSize(new Dimension(getWidth(), 60));
    }

    public ConnectionButtons(ClientGUI client) {
        this.client = client;

        this.setupPanel();
        this.createButtons();
    }

    public static void setLoggedIn() {
        m_connectButton.setEnabled(false);
        m_disconnectButton.setEnabled(true);
    }

    public static void setLoggedOut() {
        m_connectButton.setEnabled(true);
        m_disconnectButton.setEnabled(false);
    }

    public void login() {
        try {
            String nickname = JOptionPane.showInputDialog("Choisir un pseudo : ");

            if (nickname == null || nickname.isEmpty())
                return;

            client.connect(nickname);
            setLoggedIn();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logout() {
        try {
            client.leaveChat();
            setLoggedOut();
            ContentPanel.onDisconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object eventSource = e.getSource();

        if (eventSource == m_connectButton)
            login();
        else if (eventSource == m_disconnectButton)
            logout();
    }
}
