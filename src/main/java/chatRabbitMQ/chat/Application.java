package chatRabbitMQ.chat;

import chatRabbitMQ.gui.ClientFrame;

import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Application {
    public static void main(String[] args) throws IOException, TimeoutException {
        if (args.length < 1 || args.length > 2) {
            System.out.println("Usage : java Application <username> [-console]");
            return;
        }

        boolean useGUI = true;

        if (args.length == 2 && args[1].equals("-console")) {
            useGUI = false;
        }

        if (useGUI) {
            ClientGUI client = new ClientGUI();
            ClientFrame frame = new ClientFrame("Chat Application", client);

            SwingUtilities.invokeLater(() -> frame.setVisible(true));
        } else {
            new ClientConsole(args[0]);
        }
    }
}
