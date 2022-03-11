package chatRabbitMQ.server;

import com.rabbitmq.client.BuiltinExchangeType;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * This class's purpose is to configure the Rabbit-MQ Server by declaring the necessary exchanges.
 * <p>
 * The class {@link ServerClean} is used to remove these exchanges from the server.
 */
class ServerConfig extends ServerAbstract {
    @Override
    protected void configure() throws IOException {
        logger.info("Declaring exchanges...");
        this.channel.exchangeDeclare(EXCHANGE_MESSAGES_NAME, BuiltinExchangeType.FANOUT);
        logger.info("   - '" + EXCHANGE_MESSAGES_NAME + "' exchange declared");
        this.channel.exchangeDeclare(EXCHANGE_STATUS_NAME, BuiltinExchangeType.FANOUT);
        logger.info("   - '" + EXCHANGE_STATUS_NAME + "' exchange declared");
        logger.info("Exchanges declaration done");
    }

    private ServerConfig() throws IOException, TimeoutException {
        super();
        logger.info("Starting server configuration...");
        this.run();
        logger.info("Successfully configured RabbitMQ-Server !");
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        new ServerConfig();
    }
}
