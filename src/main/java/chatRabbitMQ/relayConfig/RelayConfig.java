package chatRabbitMQ.relayConfig;

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
 * This class is a special client that does not send chat messages. Its purpose is to listen and record chat messages
 * to manage the message history.
 * <p>
 * This class also creates the exchanges on login, and deletes them on logout.
 */
public class RelayConfig extends Client {
    private static final String HISTORY_PATH = System.getProperty("user.home") + "/.chatRabbitMQ/history";
    private final Vector<ChatMessage> messageList;

    private RelayConfig() throws IOException, TimeoutException {
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
        this.logShutdown("Deleting exchanges...");
        this.channel.exchangeDelete(EXCHANGE_MESSAGES_NAME);
        this.logShutdown("   - '" + EXCHANGE_MESSAGES_NAME + "' exchange deleted");
        this.channel.exchangeDelete(EXCHANGE_SYSTEM_NAME);
        this.logShutdown("   - '" + EXCHANGE_SYSTEM_NAME + "' exchange deleted");
        this.channel.exchangeDelete(EXCHANGE_NOTIFY_PRESENCE);
        this.logShutdown("   - '" + EXCHANGE_NOTIFY_PRESENCE + "' exchange deleted");
        this.channel.exchangeDelete(EXCHANGE_HISTORY);
        this.logShutdown("   - '" + EXCHANGE_HISTORY + "' exchange deleted");
        this.logShutdown("Exchanges deletion done");
        this.logShutdown("Successfully cleaned up RabbitMQ-Server !");
    }

    /**
     * Loads the message history from the disk.
     */
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

    /**
     * Saves the message history to the disk.
     */
    private void saveHistory() throws IOException {
        this.logShutdown("Saving message history...");
        File historyFile = new File(HISTORY_PATH);

        if (historyFile.getParentFile().mkdirs()) {
            this.logShutdown("Created folder '" + historyFile.getParentFile() + "'");
        }
        if (historyFile.createNewFile()) {
            this.logShutdown("Created a new history save file");
        }
        FileOutputStream oFile = new FileOutputStream(historyFile, false);
        ObjectOutputStream oOutStream = new ObjectOutputStream(oFile);

        oOutStream.writeObject(this.messageList);
        oOutStream.flush();
        oOutStream.close();
        oFile.close();

        this.logShutdown("Saved message history at '" + historyFile + "'");
    }

    @Override
    protected void messageCallback(String s, Delivery delivery) {
        ChatMessage message = ChatMessage.fromBytes(delivery.getBody());
        logger.info("Message received : " + message.getUsername() + " : " + message.getMessage());
        this.messageList.add(message);
    }

    @Override
    protected void beforeConnect() {
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
        this.logShutdown("Shutting down...");
        this.deleteExchanges();
    }

    @Override
    protected void afterDisconnect() throws IOException {
        this.saveHistory();
    }

    @Override
    protected void systemCallback(String s, Delivery delivery) {
        SystemMessage message = SystemMessage.fromBytes(delivery.getBody());
        try {
            switch (message.getType()) {
                case LOGIN: {
                    String target = message.getUuid().toString();
                    logger.info("User '" + target + "' logged in. Sending history...");

                    byte[] data = SerializationUtils.serialize(this.messageList);
                    this.channel.basicPublish(EXCHANGE_HISTORY, target, null, data);

                    logger.info("History sent");
                    break;
                }
                case LOGOUT:
                    logger.info("User '" + message.getUsername() + "' logged off");
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        new RelayConfig();
    }
}
