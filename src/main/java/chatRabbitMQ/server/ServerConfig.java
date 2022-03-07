package chatRabbitMQ.server;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ServerConfig {
    private static final String HOST = "localhost";
    private static final String EXCHANGE_MESSAGES_NAME = "messages";
    private static final String EXCHANGE_STATUS_NAME = "status";


    private Connection connection;
    private Channel channel;

    private void connect() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);

        this.connection = factory.newConnection();
        this.channel = connection.createChannel();
    }

    private void createExchanges() throws IOException {
        this.channel.exchangeDeclare(EXCHANGE_MESSAGES_NAME, BuiltinExchangeType.FANOUT);
        this.channel.exchangeDeclare(EXCHANGE_STATUS_NAME, BuiltinExchangeType.FANOUT);
    }

    private void disconnect() throws IOException, TimeoutException {
        this.channel.close();
        this.connection.close();
    }

    private ServerConfig() throws IOException, TimeoutException {
        this.connect();
        this.createExchanges();
        this.disconnect();
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        new ServerConfig();
    }
}
