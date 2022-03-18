package chatRabbitMQ.messages;

import java.util.UUID;

/**
 * A chat message is a text message sent by a user in the chat.
 *
 * @see Message
 */
public class ChatMessage extends Message {
    private final String message;
    private final ChatMessageType type;

    public ChatMessage(UUID uuid, String username, String message) {
        super(username, uuid);
        this.message = message;
        this.type = ChatMessageType.MESSAGE;
    }

    public ChatMessage(UUID uuid, String username, String message, ChatMessageType type) {
        super(username, uuid);
        this.message = message;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public static ChatMessage fromBytes(byte[] bytes) {
        return (ChatMessage) Message.fromBytes(bytes);
    }

    public String toString() {
        String formattedMessage;

        switch (this.type){
            case APPLICATION:
                formattedMessage = "<b style=\"color:#bce6bf;\">[ " + this.getUsername() + " ]</b> : " + message;
                break;
            case ERROR:
                formattedMessage = "<b style=\"color:red;\">" + this.getUsername() + "</b> : " + message;
                break;
            default:
                formattedMessage = "<b>" + this.getUsername() + "</b> : " + message;
                break;
        }
        return formattedMessage;
    }
}
