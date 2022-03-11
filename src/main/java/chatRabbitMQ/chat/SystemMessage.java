package chatRabbitMQ.chat;

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
