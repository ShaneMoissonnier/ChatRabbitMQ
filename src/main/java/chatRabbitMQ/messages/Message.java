package chatRabbitMQ.messages;

import java.io.*;
import java.util.UUID;

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

    private final UUID uuid;

    public Message(String username, UUID uuid) {
        this.username = username;
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public UUID getUuid() { return this.uuid; }

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
