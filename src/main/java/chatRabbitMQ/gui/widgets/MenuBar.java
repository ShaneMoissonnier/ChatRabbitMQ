package chatRabbitMQ.gui.widgets;

import chatRabbitMQ.gui.ClientFrame;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;

public class MenuBar extends JMenuBar {
    public MenuBar() {
        JMenu boutonFichier = new JMenu("Fichier");
        add(boutonFichier);
        boutonFichier.setMnemonic(KeyEvent.VK_F);

        boutonFichier.addSeparator();

        JMenuItem boutonQuitter = new JMenuItem("Quitter");
        boutonQuitter.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
        boutonFichier.add(boutonQuitter);

        boutonQuitter.addActionListener((ActionEvent ev) ->
        {
            if (confirmationQuitter()) {
                ClientFrame.close();
            }
        });
    }

    public boolean confirmationQuitter() {
        return JOptionPane.showConfirmDialog(
                this,
                "Voulez-vous vraiment quitter ?",
                "Quitter", JOptionPane.YES_NO_OPTION
        ) == 0;
    }
}