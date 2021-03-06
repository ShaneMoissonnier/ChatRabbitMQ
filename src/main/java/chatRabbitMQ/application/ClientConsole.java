package chatRabbitMQ.application;

import chatRabbitMQ.messages.ChatMessage;
import chatRabbitMQ.messages.SystemMessage;
import com.rabbitmq.client.Delivery;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * The main client. It represents a User using the chat in console mode to send messages.
 */
public class ClientConsole extends ChatClientAbstract {
    public ClientConsole(String username) throws IOException, TimeoutException {
        super();
        this.setClientName(username);
        this.run();
    }

    @Override
    protected void systemCallback(String consumerTag, Delivery delivery) {
        SystemMessage message = SystemMessage.fromBytes(delivery.getBody());
        switch (message.getType()) {
            case LOGIN: {
                if (!message.getUsername().equals(this.getClientName())) {
                    logger.info(message.getUsername() + " joined the chat");
                    logger.info("clients online : " + this.clients);
                }
                this.clients.put(message.getUuid(), message.getUsername());
                /* By sending a presence notification to the client that just logged in, we let them know who's online */
                this.sendPresenceNotification(message.getUuid());
                break;
            }
            case LOGOUT: {
                if (!message.getUsername().equals(this.getClientName())) {
                    logger.info(message.getUsername() + " left the chat");
                    logger.info("clients online : " + this.clients);
                }
                this.clients.remove(message.getUuid());
                break;
            }
        }
    }

    @Override
    protected void messageCallback(String consumerTag, Delivery delivery) {
        ChatMessage message = ChatMessage.fromBytes(delivery.getBody());
        System.out.println(message.getUsername() + " : " + message.getMessage());
    }

    @Override
    protected void mainBody() throws IOException {
        this.joinChat();

        String msg;
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                msg = sc.nextLine();
            } catch (NoSuchElementException e) {
                sc.close();
                break;
            }

            if (!msg.isBlank() && !msg.isEmpty()) {
                this.sendMessage(msg);
            }
        }
    }
}
