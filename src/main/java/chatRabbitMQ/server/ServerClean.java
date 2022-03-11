package chatRabbitMQ.server;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * This class's purpose is to clean up the Rabbit-MQ Server by removing the declared exchanges.
 * <p>
 * The class {@link ServerConfig} is used to configure the server.
 */
class ServerClean extends ServerAbstract {
    @Override
    protected void configure() throws IOException {
        logger.info("Deleting exchanges...");
        this.channel.exchangeDelete(EXCHANGE_MESSAGES_NAME);
        logger.info("   - '" + EXCHANGE_MESSAGES_NAME + "' exchange deleted");
        this.channel.exchangeDelete(EXCHANGE_STATUS_NAME);
        logger.info("   - '" + EXCHANGE_STATUS_NAME + "' exchange deleted");
        logger.info("Exchanges deletion done");
    }

    private ServerClean() throws IOException, TimeoutException {
        super();
        logger.info("Starting server cleaning...");
        this.run();
        logger.info("Successfully cleaned up RabbitMQ-Server !");
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        new ServerClean();
    }
}
