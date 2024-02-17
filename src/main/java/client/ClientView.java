package client;

public interface ClientView {
    void showMessage(String message);
    void disconnectFromServer();
    void connectToServer();
}
