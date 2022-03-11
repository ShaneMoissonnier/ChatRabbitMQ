package chatRabbitMQ.chat;

import java.io.*;

/**
 * This class is used by clients to communicate with one another.
 * <p>
 * There are two types of messages : <br>
 * - {@link SystemMessage} used for exchanging system information (when someone joins or leave the chat) <br>
 * - {@link ChatMessage} used for exchanging text messages sent by the users
 */
public abstract class Message implements Serializable {
    /**
     * The name of the client sending the message
     */
    private final String username;

    protected Message(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    /**
     * Converts the message to a byte array.
     * <p>
     * This method is necessary because RabbitMQ's publishing methods expect byte arrays.
     *
     * @return A byte array representing the message.
     */
    public byte[] toBytes() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(this);
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Converts a byte array to a message.
     *
     * @param bytes The bytes to convert
     * @return The deserialized message
     */
    protected static Message fromBytes(byte[] bytes) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream oin = new ObjectInputStream(bis);
            return (Message) oin.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
