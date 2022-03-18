package chatRabbitMQ;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public abstract class Client {
    protected static final String HOST = "localhost";
    protected static final String EXCHANGE_MESSAGES_NAME = "messages";
    protected static final String EXCHANGE_SYSTEM_NAME = "system";
    protected static final String EXCHANGE_NOTIFY_PRESENCE = "presence_notify";
    protected static final String EXCHANGE_HISTORY = "history";

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
     * This method exists to be overridden. It is used to allow the client to do something after connecting to the
     * server, but before subscribing to the queues
     */
    protected void afterConnect() throws IOException {
    }

    /**
     * The actual body of the client.
     * <p>
     * This is where the input loop for the main chat client is, for example.
     */
    protected void mainBody() throws IOException {
    }

    /**
     * This method exists to be overridden. It is used to allow the client to do something during the JVM shutdown
     * sequence, right before disconnecting from the server.
     */
    protected void beforeDisconnect() throws IOException {
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

    /**
     * This method exists to be overridden. It is used to allow the client to do something after disconnecting from the
     * server.
     */
    protected void afterDisconnect() throws IOException {
    }

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
     * The client's main method. It manages the flow of the client's execution
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

    protected void systemCallbackInit(String s, Delivery delivery) {
    }

    protected void messageCallbackInit(String s, Delivery delivery) {
    }

    protected void subscribeToQueue(String exchange, DeliverCallback callback, String routingKey) throws IOException {
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, exchange, routingKey);
        channel.basicConsume(queueName, true, callback, consumerTag -> {
        });
    }

    protected void subscribeToQueue(String exchange, DeliverCallback callback) throws IOException {
        this.subscribeToQueue(exchange, callback, "");
    }

    protected void subscribeToQueues() throws IOException {
        /* System queue */
        this.subscribeToQueue(EXCHANGE_SYSTEM_NAME, this::systemCallbackInit);

        /* Chat message queue */
        this.subscribeToQueue(EXCHANGE_MESSAGES_NAME, this::messageCallbackInit);
    }
}
