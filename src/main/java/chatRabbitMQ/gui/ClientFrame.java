package chatRabbitMQ.gui;

import chatRabbitMQ.chat.ClientGUI;
import chatRabbitMQ.gui.widgets.ContentPanel;
import chatRabbitMQ.gui.widgets.MenuBar;
import chatRabbitMQ.gui.widgets.SideBar;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneDarkContrastIJTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class ClientFrame extends JFrame {
    private static ClientFrame instance;

    public ClientFrame(String title, ClientGUI client){
        super(title);
        instance = this;

        //TODO: Add Flatlaf initialization
        FlatAtomOneDarkContrastIJTheme.setup();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1256, 860));

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new SideBar(client),
                new ContentPanel(client)
        );
        splitPane.setDividerLocation(250);
        this.add(splitPane);

        this.setJMenuBar(new MenuBar());

        pack();
    }

    public static void close() {
        instance.dispatchEvent(new WindowEvent(instance, WindowEvent.WINDOW_CLOSING));
    }
}
