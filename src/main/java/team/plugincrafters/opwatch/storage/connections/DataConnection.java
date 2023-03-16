package team.plugincrafters.opwatch.storage.connections;

public interface DataConnection<T> {

    T getConnection();
    void connect();
    void disconnect();
    void refreshConnection();
}
