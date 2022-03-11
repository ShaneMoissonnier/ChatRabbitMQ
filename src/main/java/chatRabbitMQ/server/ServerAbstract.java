package chatRabbitMQ.server;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public abstract class ServerAbstract {
    protected static final String HOST = "localhost";
    protected static final String EXCHANGE_MESSAGES_NAME = "messages";
    protected static final String EXCHANGE_STATUS_NAME = "status";

    protected final Logger logger;

    protected Connection connection;
    protected Channel channel;

    private void connect() throws IOException, TimeoutException {
        logger.info("Connecting to RabbitMQ-Server...");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);

        this.connection = factory.newConnection();
        this.channel = connection.createChannel();
        logger.info("Connection successful");
    }

    protected abstract void configure() throws IOException;

    private void disconnect() throws IOException, TimeoutException {
        logger.info("Disconnecting from RabbitMQ-Server...");
        this.channel.close();
        this.connection.close();
        logger.info("Disconnection successful");
    }

    protected void run() throws IOException, TimeoutException {
        this.connect();
        this.configure();
        this.disconnect();
    }

    protected ServerAbstract() {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%n");
        logger = Logger.getLogger("serverConfig");
    }
}
