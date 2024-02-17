package server;

public interface ServerRepository {
    String getHistory();
    void completeHistory(String message);
}
