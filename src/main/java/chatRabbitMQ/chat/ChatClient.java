package chatRabbitMQ.chat;

import chatRabbitMQ.common.Client;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class ChatClient extends Client {
    private final String username;

    public ChatClient(String username) throws IOException, TimeoutException {
        super();
        this.username = username;
        this.run();
    }

    private void subscribeToQueues() throws IOException {
        /* System queue */
        String systemQueueName = channel.queueDeclare().getQueue();
        channel.queueBind(systemQueueName, EXCHANGE_SYSTEM_NAME, "");
        DeliverCallback systemCallback = (consumerTag, delivery) -> {
            SystemMessage message = SystemMessage.fromBytes(delivery.getBody());
            switch (message.getType()) {
                case LOGIN -> logger.info(message.getUsername() + " joined the chat");
                case LOGOUT -> logger.info(message.getUsername() + " left the chat");
            }
        };
        channel.basicConsume(systemQueueName, true, systemCallback, consumerTag -> {});

        /* Chat message queue */
        String chatQueueName = channel.queueDeclare().getQueue();
        channel.queueBind(chatQueueName, EXCHANGE_MESSAGES_NAME, "");
        DeliverCallback chatMessageCallback = (consumerTag, delivery) -> {
            ChatMessage message = ChatMessage.fromBytes(delivery.getBody());
            System.out.println(message.getUsername() + " : " + message.getMessage());
        };
        channel.basicConsume(chatQueueName, true, chatMessageCallback, consumerTag -> {});
    }

    private void sendSystemMessage(SystemMessageType type) throws IOException {
        SystemMessage message = new SystemMessage(type, this.username);
        channel.basicPublish(EXCHANGE_SYSTEM_NAME, "", null, message.toBytes());
    }

    private void joinChat() throws IOException {
        this.sendSystemMessage(SystemMessageType.LOGIN);
    }

    private void leaveChat() throws IOException {
        this.sendSystemMessage(SystemMessageType.LOGOUT);
    }

    @Override
    protected void mainBody() throws IOException {
        this.subscribeToQueues();
        this.joinChat();

        String msg;
        ChatMessage message;
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                msg = sc.nextLine();
            } catch (NoSuchElementException e) {
                sc.close();
                break;
            }

            if (!msg.isBlank() && !msg.isEmpty()) {
                message = new ChatMessage(this.username, msg);
                channel.basicPublish(EXCHANGE_MESSAGES_NAME, "", null, message.toBytes());
            }
        }

        this.leaveChat();
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        if (args.length != 1) {
            System.out.println("Error : please provide a username");
            System.exit(1);
        }

        new ChatClient(args[0]);
    }
}
