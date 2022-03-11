package chatRabbitMQ.server;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

/**
 * This class's purpose is to configure the Rabbit-MQ Server by declaring the necessary exchanges.
 * <p>
 * The class {@link ServerClean} is used to remove these exchanges from the server.
 */
class ServerConfig {
    private static final String HOST = "localhost";
    private static final String EXCHANGE_MESSAGES_NAME = "messages";
    private static final String EXCHANGE_STATUS_NAME = "status";

    private final Logger logger;

    private Connection connection;
    private Channel channel;

    private void connect() throws IOException, TimeoutException {
        logger.info("Connecting to RabbitMQ-Server...");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);

        this.connection = factory.newConnection();
        this.channel = connection.createChannel();
        logger.info("Connection successful");
    }

    private void createExchanges() throws IOException {
        logger.info("Declaring exchanges...");
        this.channel.exchangeDeclare(EXCHANGE_MESSAGES_NAME, BuiltinExchangeType.FANOUT);
        logger.info("   - '" + EXCHANGE_MESSAGES_NAME + "' exchange declared");
        this.channel.exchangeDeclare(EXCHANGE_STATUS_NAME, BuiltinExchangeType.FANOUT);
        logger.info("   - '" + EXCHANGE_STATUS_NAME + "' exchange declared");
        logger.info("Exchanges declaration done");
    }

    private void disconnect() throws IOException, TimeoutException {
        logger.info("Disconnecting from RabbitMQ-Server...");
        this.channel.close();
        this.connection.close();
        logger.info("Disconnection successful");
    }

    private ServerConfig() throws IOException, TimeoutException {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%n");
        logger = Logger.getLogger("serverConfig");

        logger.info("Starting server configuration...");
        this.connect();
        this.createExchanges();
        this.disconnect();
        logger.info("Successfully configured RabbitMQ-Server !");
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        new ServerConfig();
    }
}
