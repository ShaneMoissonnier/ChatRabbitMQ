package chatRabbitMQ.application;

import chatRabbitMQ.Client;
import chatRabbitMQ.messages.ChatMessage;
import chatRabbitMQ.messages.Message;
import chatRabbitMQ.messages.SystemMessage;
import chatRabbitMQ.messages.SystemMessageType;
import com.rabbitmq.client.Delivery;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.util.*;

public abstract class ChatClientAbstract extends Client {
    private UUID uuid;
    private String clientName;
    protected final Map<UUID, String> clients;

    public ChatClientAbstract() {
        super();
        clients = new HashMap<>();
        this.uuid = UUID.randomUUID();
    }

    @Override
    protected void subscribeToQueues() throws IOException {
        super.subscribeToQueues();
        this.subscribeToQueue(EXCHANGE_NOTIFY_PRESENCE, this::notifyPresenceCallbackInit, this.uuid.toString());
        this.subscribeToQueue(EXCHANGE_HISTORY, this::historyCallback, this.uuid.toString());
    }

    public void joinChat() throws IOException {
        this.clients.put(this.getUuid(), this.getClientName());
        this.sendSystemMessage(SystemMessageType.LOGIN);
        logger.info("You joined the chat");
    }

    public void leaveChat() throws IOException {
        logger.info("You left the chat");
        this.sendSystemMessage(SystemMessageType.LOGOUT);
        this.clients.clear();
    }

    @Override
    protected void beforeDisconnect() throws IOException {
        this.leaveChat();
    }

    protected void notifyPresenceCallbackInit(String ignored, Delivery delivery) {
        //System.out.println(delivery.getEnvelope().getRoutingKey());
        Message m = Message.fromBytes(delivery.getBody());
        this.clients.put(m.getUuid(), m.getUsername());
        logger.info("Received a presence notification from " + m.getUsername() + ", clients online : " + this.clients);
    }

    protected void historyCallback(String s, Delivery delivery) {
        logger.info("Receiving message history from the server...");
        List<ChatMessage> messageList = SerializationUtils.deserialize(delivery.getBody());
        for (ChatMessage message : messageList) {
            /* The main reason we log instead of printing here is to make sure the output streams stay synchronized,
             * meaning that the "end of the message history" log happens after the history is actually printed */
            logger.info(message.toString());
        }
        logger.info("End of the message history");
    }

    public void sendSystemMessage(SystemMessageType type) throws IOException {
        System.out.println(this.getClientName()+ " " + this.getUuid());
        SystemMessage message = new SystemMessage(this.getUuid(), type, this.clientName);
        channel.basicPublish(EXCHANGE_SYSTEM_NAME, "", null, message.toBytes());
    }

    public void sendMessage(String msg) throws IOException {
        ChatMessage message = new ChatMessage(this.getUuid(), this.getClientName(), msg);
        channel.basicPublish(EXCHANGE_MESSAGES_NAME, "", null, message.toBytes());
    }

    public void sendPresenceNotification(UUID uuid) {
        Message message = new Message(this.getClientName(), this.uuid);
        try {
            channel.basicPublish(EXCHANGE_NOTIFY_PRESENCE, uuid.toString(), null, message.toBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UUID getUuid() { return this.uuid; }

    public String getClientName() {
        return this.clientName;
    }

    public void setClientName(String name) {
        this.clientName = name;
    }
}
