package chatRabbitMQ.messages;

/**
 * A system message is anything that is not a {@link ChatMessage}, i.e. a message to notify that someone joined or left
 * the chat.
 *
 * @see Message
 */
public class SystemMessage extends Message {
    private final SystemMessageType type;

    public SystemMessage(SystemMessageType type, String username) {
        super(username);
        this.type = type;
    }

    public SystemMessageType getType() {
        return type;
    }

    public static SystemMessage fromBytes(byte[] bytes) {
        return (SystemMessage) Message.fromBytes(bytes);
    }
}
