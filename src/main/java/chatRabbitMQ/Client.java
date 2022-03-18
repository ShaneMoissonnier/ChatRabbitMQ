package chatRabbitMQ;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

/**
 * This class represents the core of what a client is.
 * <p>
 * It manages the connection / disconnection to the RabbitMQ server, the logger, and everything else that is common to
 * every type of client.
 *
 * @see chatRabbitMQ.application.ChatClientAbstract
 * @see chatRabbitMQ.application.ClientConsole
 * @see chatRabbitMQ.application.ClientGUI
 * @see chatRabbitMQ.relayConfig.RelayConfig
 */
public abstract class Client {
    /* RabbitMQ Server host */
    protected static final String HOST = "localhost";

    /* Names of the different exchanges */
    protected static final String EXCHANGE_MESSAGES_NAME = "messages"; /* Used to send text messages */
    protected static final String EXCHANGE_SYSTEM_NAME = "system"; /* Used to send login/logout messages */
    protected static final String EXCHANGE_NOTIFY_PRESENCE = "presence_notify"; /* Used to notify newcomers of our presence */
    protected static final String EXCHANGE_HISTORY = "history"; /* Used by the relay to send the message history to newcomers */

    protected Logger logger = null;

    protected Connection connection = null;
    protected Channel channel = null;

    private void setupLogger() {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%n");
        System.setProperty("java.util.logging.manager", MyLogManager.class.getName());
        logger = Logger.getLogger(this.getClass().getName());
    }

    /**
     * This method exists to be overridden. It is used to allow the client to do something before connecting to the
     * server.
     */
    protected void beforeConnect() {
    }

    /**
     * Connects to the RabbitMQ Server.
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
     * This method exists to be overridden. It is used to allow the client to do something after connecting to the
     * server, but before subscribing to the queues.
     */
    protected void afterConnect() throws IOException {
    }

    /**
     * The actual body of the client.
     * <p>
     * This is where the input loop for the main chat client in console mode is, for example.
     */
    protected void mainBody() throws IOException {
    }

    /**
     * This method exists to be overridden. It is used to allow the client to do something during the JVM shutdown
     * sequence, right before disconnecting from the RabbitMQ server.
     */
    protected void beforeDisconnect() throws IOException {
    }

    /**
     * Disconnects from the RabbitMQ Server.
     */
    private void disconnect() throws IOException, TimeoutException {
        logger.info("Disconnecting from RabbitMQ-Server...");
        this.channel.close();
        this.connection.close();
        logger.info("Disconnection successful");
    }

    /**
     * This method exists to be overridden. It is used to allow the client to do something after disconnecting from the
     * server.
     */
    protected void afterDisconnect() throws IOException {
    }

    /**
     * This method is called during the JVM shutdown sequence.
     */
    private void shutdown() {
        try {
            this.beforeDisconnect();
            this.disconnect();
            this.afterDisconnect();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
        MyLogManager.resetFinally();
    }

    /**
     * The client's main method. It manages the flow of the client's execution.
     */
    protected void run() throws IOException, TimeoutException {
        this.setupLogger();
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        this.beforeConnect();
        this.connect();
        this.afterConnect();

        this.subscribeToQueues();
        this.mainBody();
    }

    /**
     * This method is a callback called whenever someone sends a system message (self included)
     *
     * @param s        The consumer tag
     * @param delivery The content of the message
     */
    protected void systemCallback(String s, Delivery delivery) {
    }

    /**
     * This method is a callback called whenever someone sends a chat message (self included)
     *
     * @param s        The consumer tag
     * @param delivery The content of the message
     */
    protected void messageCallback(String s, Delivery delivery) {
    }

    /**
     * This method creates a queue, binds it to an exchanges and links the callback.
     *
     * @param exchange   The exchange we want to bind the new queue to
     * @param callback   The callback used to consume from the queue
     * @param routingKey The routing queue used to bind the que to the exchange
     */
    protected void subscribeToQueue(String exchange, DeliverCallback callback, String routingKey) throws IOException {
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, exchange, routingKey);
        channel.basicConsume(queueName, true, callback, consumerTag -> {
        });
    }

    /**
     * Same as {@link Client#subscribeToQueue(String, DeliverCallback, String)}, but without a routing key.
     */
    protected void subscribeToQueue(String exchange, DeliverCallback callback) throws IOException {
        this.subscribeToQueue(exchange, callback, "");
    }

    /**
     * This method creates the common queues (system and messages), binds them and subscribe to them.
     */
    protected void subscribeToQueues() throws IOException {
        /* System queue */
        this.subscribeToQueue(EXCHANGE_SYSTEM_NAME, this::systemCallback);

        /* Chat message queue */
        this.subscribeToQueue(EXCHANGE_MESSAGES_NAME, this::messageCallback);
    }
}
