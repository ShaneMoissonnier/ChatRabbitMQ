package chatRabbitMQ.config;

import chatRabbitMQ.common.Client;
import com.rabbitmq.client.BuiltinExchangeType;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * This class's purpose is to configure the RabbitMQ Server by declaring the necessary exchanges.
 * <p>
 * The class {@link ConfigClean} is used to remove these exchanges from the server.
 */
class ConfigSetup extends Client {
    private ConfigSetup() throws IOException, TimeoutException {
        super();
        this.run();
    }

    @Override
    protected void mainBody() throws IOException {
        logger.info("Declaring exchanges...");
        this.channel.exchangeDeclare(EXCHANGE_MESSAGES_NAME, BuiltinExchangeType.FANOUT);
        logger.info("   - '" + EXCHANGE_MESSAGES_NAME + "' exchange declared");
        this.channel.exchangeDeclare(EXCHANGE_SYSTEM_NAME, BuiltinExchangeType.FANOUT);
        logger.info("   - '" + EXCHANGE_SYSTEM_NAME + "' exchange declared");
        logger.info("Exchanges declaration done");
    }

    @Override
    protected void beforeConnect() {
        logger.info("Starting server configuration...");
    }

    @Override
    protected void afterDisconnect() {
        logger.info("Successfully configured RabbitMQ-Server !");
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        new ConfigSetup();
    }
}
