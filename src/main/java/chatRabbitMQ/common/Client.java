package chatRabbitMQ.common;

import chatRabbitMQ.chat.SystemMessage;
import chatRabbitMQ.chat.SystemMessageType;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public abstract class Client {
    protected static final String HOST = "localhost";
    protected static final String EXCHANGE_MESSAGES_NAME = "messages";
    protected static final String EXCHANGE_SYSTEM_NAME = "system";
    protected static final String EXCHANGE_NOTIFY_PRESENCE = "presence_notify";

    protected Logger logger = null;

    protected Connection connection = null;
    protected Channel channel = null;

    private String clientName;
    protected final Set<String> clients;

    private void setupLogger() {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%n");
        logger = Logger.getLogger(this.getClass().getName());
    }

    /**
     * Connects to the RabbitMQ Server
     */
    private void connect() throws IOException, TimeoutException {
        logger.info("Connecting to RabbitMQ-Server...");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);

        this.connection = factory.newConnection();
        this.channel = connection.createChannel();
        logger.info("Connection successful");
    }

    /**
     * Disconnects from the RabbitMQ Server
     */
    private void disconnect() throws IOException, TimeoutException {
        logger.info("Disconnecting from RabbitMQ-Server...");
        this.channel.close();
        this.connection.close();
        logger.info("Disconnection successful");
    }

    private void subscribeToQueues() throws IOException {
        /* System queue */
        String systemQueueName = channel.queueDeclare().getQueue();
        channel.queueBind(systemQueueName, EXCHANGE_SYSTEM_NAME, "");
        channel.basicConsume(systemQueueName, true, this::systemCallbackInit, consumerTag -> {
        });

        /* Chat message queue */
        String chatQueueName = channel.queueDeclare().getQueue();
        channel.queueBind(chatQueueName, EXCHANGE_MESSAGES_NAME, "");
        channel.basicConsume(chatQueueName, true, this::messageCallbackInit, consumerTag -> {
        });

        /* Presence notification queue */
        String notifyQueueName = channel.queueDeclare().getQueue();
        channel.queueBind(notifyQueueName, EXCHANGE_NOTIFY_PRESENCE, this.getClientName());
        channel.basicConsume(notifyQueueName, true, this::notifyPresenceCallbackInit, consumerTag -> {
        });
    }

    protected abstract void systemCallbackInit(String s, Delivery delivery);

    protected abstract void messageCallbackInit(String s, Delivery delivery);

    protected void notifyPresenceCallbackInit(String ignored, Delivery delivery) {
        Message m = Message.fromBytes(delivery.getBody());
        this.clients.add(m.getUsername());
        logger.info("clients online : " + this.clients);
    }

    /**
     * This method exists to be overridden. It is used to allow the client to do something before connecting to the
     * server.
     */
    protected void beforeConnect() {
    }

    /**
     * The actual body of the client.
     * <p>
     * This is where the input loop for the main chat client is, for example.
     */
    protected abstract void mainBody() throws IOException;

    /**
     * This method exists to be overridden. It is used to allow the client to do something after disconnecting to the
     * server.
     */
    protected void afterDisconnect() {
    }

    /**
     * The client's main method. It manages the flow of the client's execution
     */
    protected void run(boolean client) throws IOException, TimeoutException {
        this.setupLogger();
        this.beforeConnect();
        this.connect();
        if (client)
            this.subscribeToQueues();
        this.mainBody();
        this.disconnect();
        this.afterDisconnect();
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

    public void joinChat() throws IOException {
        this.clients.add(this.clientName);
        this.sendSystemMessage(SystemMessageType.LOGIN);
    }

    public void leaveChat() throws IOException {
        this.sendSystemMessage(SystemMessageType.LOGOUT);
        this.clients.clear();
    }

    public String getClientName() {
        return this.clientName;
    }

    public void setClientName(String name) {
        this.clientName = name;
    }

    public Client() {
        clients = new HashSet<>();
    }
}
