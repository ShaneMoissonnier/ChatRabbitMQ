package chatRabbitMQ.server;

import chatRabbitMQ.Client;
import chatRabbitMQ.messages.ChatMessage;
import chatRabbitMQ.messages.SystemMessage;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Delivery;
import org.apache.commons.lang3.SerializationUtils;

import java.io.*;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeoutException;

/**
 * This class's purpose is to configure the RabbitMQ Server by declaring the necessary exchanges.
 * <p>
 * The class is used to remove these exchanges from the server.
 */
class Server extends Client {
    private static final String HISTORY_PATH = System.getProperty("user.home") + "/.chatRabbitMQ/history";
    private final Vector<ChatMessage> messageList;

    private Server() throws IOException, TimeoutException {
        super();
        this.messageList = new Vector<>();
        this.run();
    }

    private void setupExchanges() throws IOException {
        logger.info("Declaring exchanges...");
        this.channel.exchangeDeclare(EXCHANGE_MESSAGES_NAME, BuiltinExchangeType.FANOUT);
        logger.info("   - '" + EXCHANGE_MESSAGES_NAME + "' exchange declared");
        this.channel.exchangeDeclare(EXCHANGE_SYSTEM_NAME, BuiltinExchangeType.FANOUT);
        logger.info("   - '" + EXCHANGE_SYSTEM_NAME + "' exchange declared");
        this.channel.exchangeDeclare(EXCHANGE_NOTIFY_PRESENCE, BuiltinExchangeType.DIRECT);
        logger.info("   - '" + EXCHANGE_NOTIFY_PRESENCE + "' exchange declared");
        this.channel.exchangeDeclare(EXCHANGE_HISTORY, BuiltinExchangeType.DIRECT);
        logger.info("   - '" + EXCHANGE_HISTORY + "' exchange declared");
        logger.info("Exchanges declaration done");
        logger.info("Successfully configured RabbitMQ-Server !");
    }

    private void deleteExchanges() throws IOException {
        logger.info("Deleting exchanges...");
        this.channel.exchangeDelete(EXCHANGE_MESSAGES_NAME);
        logger.info("   - '" + EXCHANGE_MESSAGES_NAME + "' exchange deleted");
        this.channel.exchangeDelete(EXCHANGE_SYSTEM_NAME);
        logger.info("   - '" + EXCHANGE_SYSTEM_NAME + "' exchange deleted");
        this.channel.exchangeDelete(EXCHANGE_NOTIFY_PRESENCE);
        logger.info("   - '" + EXCHANGE_NOTIFY_PRESENCE + "' exchange deleted");
        this.channel.exchangeDelete(EXCHANGE_HISTORY);
        logger.info("   - '" + EXCHANGE_HISTORY + "' exchange deleted");
        logger.info("Exchanges deletion done");
        logger.info("Successfully cleaned up RabbitMQ-Server !");
    }

    private void loadHistory() throws IOException, ClassNotFoundException {
        logger.info("Loading message history...");
        File historyFile = new File(HISTORY_PATH);

        if (!historyFile.exists()) {
            logger.info("No message history found");
            return;
        }

        FileInputStream iFile = new FileInputStream(historyFile);
        ObjectInputStream oInputStream = new ObjectInputStream(iFile);

        List<?> objectList = (List<?>) oInputStream.readObject();
        if (objectList == null) {
            logger.severe("Empty history file");
            return;
        }

        this.messageList.clear();
        for (Object o : objectList) {
            if (!(o instanceof ChatMessage)) {
                logger.severe(("Corrupt history file"));
                if (historyFile.delete()) {
                    logger.info("History file deleted");
                }
                return;
            }

            this.messageList.add((ChatMessage) o);
        }

        logger.info("Message history successfully loaded");
    }

    private void saveHistory() throws IOException {
        logger.info("Saving message history...");
        File historyFile = new File(HISTORY_PATH);

        if (historyFile.getParentFile().mkdirs()) {
            logger.info("Created folder '" + historyFile.getParentFile() + "'");
        }
        if (historyFile.createNewFile()) {
            logger.info("Created a new history save file");
        }
        FileOutputStream oFile = new FileOutputStream(historyFile, false);
        ObjectOutputStream oOutStream = new ObjectOutputStream(oFile);

        oOutStream.writeObject(this.messageList);
        oOutStream.flush();
        oOutStream.close();
        oFile.close();

        logger.info("Saved message history at '" + historyFile + "'");
    }

    @Override
    protected void messageCallbackInit(String s, Delivery delivery) {
        ChatMessage message = ChatMessage.fromBytes(delivery.getBody());
        logger.info("Message received : " + message.getUsername() + " : " + message.getMessage());
        this.messageList.add(message);
    }

    @Override
    protected void beforeConnect() {
        logger.info("Starting the server...");
        logger.info("Starting server configuration...");
        try {
            this.loadHistory();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void afterConnect() throws IOException {
        this.setupExchanges();
        logger.info("Starting listening for messages");
    }

    @Override
    protected void beforeDisconnect() throws IOException {
        logger.info("Shutting down...");
        this.deleteExchanges();
    }

    @Override
    protected void afterDisconnect() throws IOException {
        this.saveHistory();
    }

    @Override
    protected void systemCallbackInit(String s, Delivery delivery) {
        SystemMessage message = SystemMessage.fromBytes(delivery.getBody());
        try {
            switch (message.getType()) {
                case LOGIN -> {
                    String target = message.getUsername();
                    logger.info("User '" + target + "' logged in. Sending history...");

                    byte[] data = SerializationUtils.serialize(this.messageList);
                    this.channel.basicPublish(EXCHANGE_HISTORY, target, null, data);

                    logger.info("History sent");
                }
                case LOGOUT -> logger.info("User '" + message.getUsername() + "' logged off");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        new Server();
    }
}
