package chatRabbitMQ.config;

import chatRabbitMQ.common.Client;
import com.rabbitmq.client.Delivery;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * This class's purpose is to clean up the RabbitMQ Server by removing the declared exchanges.
 * <p>
 * The class {@link ConfigSetup} is used to configure the server.
 */
class ConfigClean extends Client {
    public ConfigClean() throws IOException, TimeoutException {
        super();
        this.run(false);
    }

    @Override
    protected void mainBody() throws IOException {
        logger.info("Deleting exchanges...");
        this.channel.exchangeDelete(EXCHANGE_MESSAGES_NAME);
        logger.info("   - '" + EXCHANGE_MESSAGES_NAME + "' exchange deleted");
        this.channel.exchangeDelete(EXCHANGE_SYSTEM_NAME);
        logger.info("   - '" + EXCHANGE_SYSTEM_NAME + "' exchange deleted");
        this.channel.exchangeDelete(EXCHANGE_NOTIFY_PRESENCE);
        logger.info("   - '" + EXCHANGE_NOTIFY_PRESENCE + "' exchange deleted");
        logger.info("Exchanges deletion done");
    }

    @Override
    protected void systemCallbackInit(String s, Delivery delivery) {

    }

    @Override
    protected void messageCallbackInit(String s, Delivery delivery) {

    }

    @Override
    protected void beforeConnect() {
        logger.info("Starting server cleaning...");
    }

    @Override
    protected void afterDisconnect() {
        logger.info("Successfully cleaned up RabbitMQ-Server !");
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        new ConfigClean();
    }
}
