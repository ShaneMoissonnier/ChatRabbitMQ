package chatRabbitMQ.chat;

import chatRabbitMQ.common.Client;
import chatRabbitMQ.gui.widgets.ContentPanel;
import com.rabbitmq.client.Delivery;

import java.io.IOException;

public class ClientGUI extends Client {
    private boolean loggedIn;

    public ClientGUI() {
        loggedIn = false;
    }

    /*@Override
    protected void shutDown() throws RemoteException {
        if (loggedIn) {
            this.logout();
        }
    }

    @Override
    protected void loadHistory(List<Message> history) throws RemoteException {
        for (Message m : history) {
            ContentPanel.addMessage(m);
        }
    }

    @Override
    public boolean isLoggedIn() throws RemoteException {
        return loggedIn;
    }

    @Override
    public void loginCallback(boolean status, List<Message> history) throws RemoteException {
        super.loginCallback(status, history);

        if (status) {
            this.loggedIn = true;
            SideBar.onSelfLogin(this.chatService.getLoggedInClients());
            ConnectionButtons.setLoggedIn();
        }
    }

    @Override
    public void otherLoginCallback(ClientInfo other) throws RemoteException {
        super.otherLoginCallback(other);
        SideBar.onOtherLogin(other);
    }

    @Override
    public void logoutCallback(boolean status) throws RemoteException {
        super.logoutCallback(status);

        if (status) {
            this.loggedIn = false;
            ContentPanel.onDisconnect();
            SideBar.onSelfLogout();
            ConnectionButtons.setLoggedOut();
        }
    }

    @Override
    public void otherLogoutCallback(ClientInfo other) throws RemoteException {
        super.otherLogoutCallback(other);
        SideBar.onOtherLogout(other);
    }*/

    @Override
    protected void systemCallbackInit(String s, Delivery delivery) {
        SystemMessage message = SystemMessage.fromBytes(delivery.getBody());
        switch (message.getType()) {
            case LOGIN -> logger.info(message.getUsername() + " joined the chat");
            case LOGOUT -> logger.info(message.getUsername() + " left the chat");
        }
    }

    @Override
    protected void messageCallbackInit(String s, Delivery delivery) {
        ChatMessage message = ChatMessage.fromBytes(delivery.getBody());
        ContentPanel.addMessage(message);
    }

    @Override
    protected void mainBody() {

    }
}
