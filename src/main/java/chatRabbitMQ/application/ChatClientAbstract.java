package chatRabbitMQ.application;

import chatRabbitMQ.Client;
import chatRabbitMQ.messages.ChatMessage;
import chatRabbitMQ.messages.Message;
import chatRabbitMQ.messages.SystemMessage;
import chatRabbitMQ.messages.SystemMessageType;
import com.rabbitmq.client.Delivery;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ChatClientAbstract extends Client {
    private String clientName;
    protected final Set<String> clients;

    public ChatClientAbstract() {
        super();
        clients = new HashSet<>();
    }

    @Override
    protected void subscribeToQueues() throws IOException {
        super.subscribeToQueues();
        this.subscribeToQueue(EXCHANGE_NOTIFY_PRESENCE, this::notifyPresenceCallbackInit, this.getClientName());
        this.subscribeToQueue(EXCHANGE_HISTORY, this::historyCallback, this.getClientName());
    }

    public void joinChat() throws IOException {
        this.clients.add(this.getClientName());
        this.sendSystemMessage(SystemMessageType.LOGIN);
        logger.info(this.getClientName() + " joined the chat");
    }

    public void leaveChat() throws IOException {
        this.sendSystemMessage(SystemMessageType.LOGOUT);
        this.clients.clear();
    }

    @Override
    protected void beforeDisconnect() throws IOException {
        this.leaveChat();
    }

    protected void notifyPresenceCallbackInit(String ignored, Delivery delivery) {
        Message m = Message.fromBytes(delivery.getBody());
        this.clients.add(m.getUsername());
        logger.info("Received a presence notification from " + m.getUsername() + ", clients online : " + this.clients);
    }

    protected void historyCallback(String s, Delivery delivery) {
        logger.info("Receiving message history from the server...");
        List<ChatMessage> messageList = SerializationUtils.deserialize(delivery.getBody());
        for (ChatMessage message : messageList) {
            /* The main reason we log instead of printing here is to make sure the output streams stay synchronized,
             * meaning that the "end of the message history" log happens after the history is actually printed */
            logger.info("   " + message.getUsername() + " : " + message.getMessage());
        }
        logger.info("End of the message history");
    }

    public void sendSystemMessage(SystemMessageType type) throws IOException {
        SystemMessage message = new SystemMessage(type, this.clientName);
        channel.basicPublish(EXCHANGE_SYSTEM_NAME, "", null, message.toBytes());
    }

    public void sendPresenceNotification(String target) {
        Message message = new Message(this.getClientName());
        try {
            channel.basicPublish(EXCHANGE_NOTIFY_PRESENCE, target, null, message.toBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getClientName() {
        return this.clientName;
    }

    public void setClientName(String name) {
        this.clientName = name;
    }
}
