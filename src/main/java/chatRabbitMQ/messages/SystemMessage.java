package chatRabbitMQ.messages;

import java.util.UUID;

/**
 * A system message is anything that is not a {@link ChatMessage}, i.e. a message to notify that someone joined or left
 * the chat.
 *
 * @see Message
 */
public class SystemMessage extends Message {
    private final SystemMessageType type;

    public SystemMessage(UUID uuid, SystemMessageType type, String username) {
        super(username, uuid);
        this.type = type;
    }

    public SystemMessageType getType() {
        return type;
    }

    public static SystemMessage fromBytes(byte[] bytes) {
        return (SystemMessage) Message.fromBytes(bytes);
    }
}
