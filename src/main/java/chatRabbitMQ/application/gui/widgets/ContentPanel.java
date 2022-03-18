package chatRabbitMQ.application.gui.widgets;

import chatRabbitMQ.messages.Message;
import chatRabbitMQ.application.ClientGUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.io.IOException;

public class ContentPanel extends JPanel {
    private static final JTextPane textArea = new JTextPane();

    private void setupTextArea() {
        this.add(textArea, BorderLayout.CENTER);
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setMargin(new Insets(20, 20, 20, 20));
        textArea.setContentType("text/html");
        textArea.setBorder(new EmptyBorder(new Insets(0,0,0,0)));
    }

    private void setupScrollPane(ClientGUI client) {
        JScrollPane scrollPane = new JScrollPane(textArea);
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(new SendMessageBar(client), BorderLayout.SOUTH);
    }

    private void setupChannelName() {
        JLabel label = new JLabel();
        label.setText("<html><b>Channel: </b> General</html>");
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
        label.setBackground(new Color(0x262626));
        label.setOpaque(true);

        this.add(label, BorderLayout.NORTH);
    }

    public ContentPanel(ClientGUI client) {
        this.setLayout(new BorderLayout());

        setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));

        this.setupTextArea();
        this.setupScrollPane(client);
        this.setupChannelName();
    }

    public static void addMessage(Message message) {
        try {
            HTMLDocument document = (HTMLDocument)textArea.getDocument();
            HTMLEditorKit editorKit = (HTMLEditorKit)textArea.getEditorKit();
            editorKit.insertHTML(document, document.getLength(), message.toString() + "\n",0, 0, null);
            textArea.setCaretPosition(document.getLength());
        } catch (BadLocationException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void onDisconnect() {
        textArea.setText("");
    }
}
