package chatRabbitMQ.server;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

/**
 * This class's purpose is to clean up the Rabbit-MQ Server by removing the declared exchanges.
 * <p>
 * The class {@link ServerConfig} is used to configure the server.
 */
class ServerClean {
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

    private void deleteExchanges() throws IOException {
        logger.info("Deleting exchanges...");
        this.channel.exchangeDelete(EXCHANGE_MESSAGES_NAME);
        logger.info("   - '" + EXCHANGE_MESSAGES_NAME + "' exchange deleted");
        this.channel.exchangeDelete(EXCHANGE_STATUS_NAME);
        logger.info("   - '" + EXCHANGE_STATUS_NAME + "' exchange deleted");
        logger.info("Exchanges deletion done");
    }

    private void disconnect() throws IOException, TimeoutException {
        logger.info("Disconnecting from RabbitMQ-Server...");
        this.channel.close();
        this.connection.close();
        logger.info("Disconnection successful");
    }

    private ServerClean() throws IOException, TimeoutException {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%n");
        logger = Logger.getLogger("serverConfig");

        logger.info("Starting server cleaning...");
        this.connect();
        this.deleteExchanges();
        this.disconnect();
        logger.info("Successfully cleaned up RabbitMQ-Server !");
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        new ServerClean();
    }
}
