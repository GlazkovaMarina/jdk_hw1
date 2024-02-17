package client;

import server.Server;

public class Client {
    private ClientView clientView;
    private boolean isConnected;
    private Server server;
    private String name;

    public Client(ClientView clientView, Server server) {
        this.clientView = clientView;
        this.server = server;
    }

    public boolean connectToServer(String name){
        this.name = name;
        if (server.connectUser(this)){
            showMessage("Вы успешно подключились!\n");
            isConnected = true;
            String log = server.getHistory();
            if (log != null){
                showMessage(log);
            }
            return true;
        } else {
            showMessage("Подключение не удалось");
            return false;
        }
    }

    private void showMessage(String message){
        clientView.showMessage(message);
    }

    public boolean disconnectFromServer() {
        if (isConnected) {
            isConnected = false;
            server.disconnectUser(this);
            clientView.disconnectFromServer();
            showMessage("Вы отключены от сервера!");
            return true;
        }
        return false;
    }
    public void answerFromServer(String message){
        showMessage(message);
    }
    public void sendMessage(String message){
        if (isConnected){
            if (!message.isEmpty()){
                server.message(name + ": " + message);
            }
            else{
                showMessage("Введите сообщение!");
            }
        } else {
            showMessage("Нет подключения к серверу");
        }
    }
    public String getUserName(){
        return name;
    }
}
