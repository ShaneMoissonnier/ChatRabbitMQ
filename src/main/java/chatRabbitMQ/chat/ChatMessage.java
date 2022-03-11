package chatRabbitMQ.chat;

public class ChatMessage extends Message {
    private final String message;

    protected ChatMessage(String username, String message) {
        super(username);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static ChatMessage fromBytes(byte[] bytes) {
        return (ChatMessage) Message.fromBytes(bytes);
    }
}
