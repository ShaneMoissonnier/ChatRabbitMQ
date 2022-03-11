package chatRabbitMQ.common;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public abstract class Client {
    protected static final String HOST = "localhost";
    protected static final String EXCHANGE_MESSAGES_NAME = "messages";
    protected static final String EXCHANGE_SYSTEM_NAME = "system";

    protected Logger logger = null;

    protected Connection connection = null;
    protected Channel channel = null;

    private void setupLogger() {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%n");
        logger = Logger.getLogger(this.getClass().getName());
    }

    private void connect() throws IOException, TimeoutException {
        logger.info("Connecting to RabbitMQ-Server...");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);

        this.connection = factory.newConnection();
        this.channel = connection.createChannel();
        logger.info("Connection successful");
    }

    private void disconnect() throws IOException, TimeoutException {
        logger.info("Disconnecting from RabbitMQ-Server...");
        this.channel.close();
        this.connection.close();
        logger.info("Disconnection successful");
    }

    protected void beforeConnect() {
    }

    protected abstract void mainBody() throws IOException;

    protected void afterDisconnect() {
    }

    protected void run() throws IOException, TimeoutException {
        this.setupLogger();
        this.beforeConnect();
        this.connect();
        this.mainBody();
        this.disconnect();
        this.afterDisconnect();
    }

    public Client() {
    }
}
