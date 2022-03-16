package chatRabbitMQ.messages;

import java.io.*;

import org.apache.commons.lang3.SerializationUtils;

/**
 * This class is used by clients to communicate with one another.
 * <p>
 * There are two types of messages : <br>
 * - {@link SystemMessage} used for exchanging system information (when someone joins or leave the chat) <br>
 * - {@link ChatMessage} used for exchanging text messages sent by the users
 */
public class Message implements Serializable {
    /**
     * The name of the client sending the message
     */
    private final String username;

    public Message(String username) {
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
        return SerializationUtils.serialize(this);
    }

    /**
     * Converts a byte array to a message.
     *
     * @param bytes The bytes to convert
     * @return The deserialized message
     */
    public static Message fromBytes(byte[] bytes) {
        return SerializationUtils.deserialize(bytes);
    }
}
