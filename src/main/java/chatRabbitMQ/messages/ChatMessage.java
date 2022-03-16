package chatRabbitMQ.messages;

/**
 * A chat message is a text message sent by a user in the chat.
 *
 * @see Message
 */
public class ChatMessage extends Message {
    private final String message;

    public ChatMessage(String username, String message) {
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
