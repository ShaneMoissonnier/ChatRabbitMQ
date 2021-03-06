package chatRabbitMQ.application;

import chatRabbitMQ.application.gui.widgets.ContentPanel;
import chatRabbitMQ.application.gui.widgets.SideBar;
import chatRabbitMQ.messages.ChatMessage;
import chatRabbitMQ.messages.ChatMessageType;
import chatRabbitMQ.messages.Message;
import chatRabbitMQ.messages.SystemMessage;
import com.rabbitmq.client.Delivery;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class ClientGUI extends ChatClientAbstract {
    private boolean loggedIn;
    private boolean initialized;

    public ClientGUI() {
        loggedIn = false;
        initialized = false;
    }

    @Override
    protected void systemCallback(String s, Delivery delivery) {
        SystemMessage message = SystemMessage.fromBytes(delivery.getBody());
        switch (message.getType()) {
            case LOGIN:
                this.onLogin(message);
                break;
            case LOGOUT:
                this.onLogout(message);
                break;
        }
    }

    @Override
    protected void messageCallback(String s, Delivery delivery) {
        ChatMessage message = ChatMessage.fromBytes(delivery.getBody());
        ContentPanel.addMessage(message);
    }

    @Override
    protected void historyCallback(String s, Delivery delivery) {
        ContentPanel.addMessage(new ChatMessage(null, "Application", "Beginning of the message history", ChatMessageType.APPLICATION));
        List<ChatMessage> messageList = SerializationUtils.deserialize(delivery.getBody());
        for (ChatMessage message : messageList) {
            ContentPanel.addMessage(message);
        }
        ContentPanel.addMessage(new ChatMessage(null, "Application", "End of the message history", ChatMessageType.APPLICATION));
    }

    protected void notifyPresenceCallback(String ignored, Delivery delivery) {
        if (!isLoggedIn())
            return;

        Message message = Message.fromBytes(delivery.getBody());
        String username = message.getUsername();
        UUID uuid = message.getUuid();

        this.clients.put(uuid, username);
        SideBar.addClientToList(this.clients);
    }

    @Override
    protected void mainBody() throws IOException {
        this.joinChat();
    }

    public void connect(String username) throws IOException, TimeoutException {
        this.setClientName(username);

        if (!initialized) {
            this.run();
            this.initialized = true;
            return;
        }
        this.joinChat();
    }

    protected void onLogin(SystemMessage message) {
        UUID messageUuid = message.getUuid();
        UUID myUuid = this.getUuid();
        String username = message.getUsername();

        if (messageUuid.toString().equals(myUuid.toString())) {
            this.loggedIn = true;
            this.clients.clear();
        }

        if (!loggedIn)
            return;

        this.clients.put(messageUuid, username);
        this.sendPresenceNotification(messageUuid);
        SideBar.addClientToList(this.clients);
        ContentPanel.addMessage(new ChatMessage(null, "Application", username + " joined the chat", ChatMessageType.APPLICATION));
    }

    protected void onLogout(SystemMessage message) {
        UUID messageUuid = message.getUuid();
        UUID myUuid = this.getUuid();
        String username = message.getUsername();

        if (messageUuid.toString().equals(myUuid.toString())) {
            this.loggedIn = false;
            SideBar.onDisconnect();
            this.clients.clear();
        }

        if (!this.loggedIn)
            return;

        this.clients.remove(messageUuid);
        SideBar.removeClientFromList(username);
        ContentPanel.addMessage(new ChatMessage(null, "Application", username + " left the chat", ChatMessageType.APPLICATION));
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    @Override
    protected void beforeDisconnect() throws IOException {
        if (this.isLoggedIn()) {
            super.beforeDisconnect();
        }
    }
}
