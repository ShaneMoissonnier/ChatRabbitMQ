package chatRabbitMQ.application;

import chatRabbitMQ.application.gui.widgets.ContentPanel;
import chatRabbitMQ.messages.ChatMessage;
import chatRabbitMQ.messages.SystemMessage;
import com.rabbitmq.client.Delivery;

public class ClientGUI extends ChatClientAbstract {
    private boolean loggedIn;

    public ClientGUI() {
        loggedIn = false;

        // TODO this.run();
    }

    @Override
    protected void systemCallbackInit(String s, Delivery delivery) {
        SystemMessage message = SystemMessage.fromBytes(delivery.getBody());
        switch (message.getType()) {
            case LOGIN -> logger.info(message.getUsername() + " joined the chat");
            case LOGOUT -> logger.info(message.getUsername() + " left the chat");
        }
    }

    @Override
    protected void messageCallbackInit(String s, Delivery delivery) {
        ChatMessage message = ChatMessage.fromBytes(delivery.getBody());
        ContentPanel.addMessage(message);
    }

    @Override
    protected void mainBody() {

    }
}
