package chatRabbitMQ.application.gui.widgets;

import chatRabbitMQ.application.ClientGUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SideBar extends JPanel {
    private static DefaultListModel<String> model;

    public SideBar(ClientGUI client) {
        this.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));

        model = new DefaultListModel<>();
        JList<String> list = new JList<>(model);

        JLabel label = setupChannelLabel();

        panel.add(label, BorderLayout.NORTH);
        panel.add(list, BorderLayout.CENTER);

        ConnectionButtons connectionButtons = new ConnectionButtons(client);

        this.add(panel, BorderLayout.CENTER);
        this.add(connectionButtons, BorderLayout.SOUTH);
    }

    private JLabel setupChannelLabel() {
        JLabel label = new JLabel();
        label.setText("Utilisateurs actifs");
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
        label.setBackground(new Color(0x262626));
        label.setOpaque(true);

        return label;
    }

    public static void addClientToList(Map<UUID, String> clients) {
        Set<UUID> clientsUuid = clients.keySet();
        model.clear();
        for (UUID clientUuid : clientsUuid) {
            String username = clients.get(clientUuid);
            model.addElement(username);
        }
    }

    public static void onDisconnect() {
        model.removeAllElements();
    }

    public static void removeClientFromList(String client) {
        if ( ! model.contains(client))
            return;

        model.removeElement(client);
    }
}
