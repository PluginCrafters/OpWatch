package team.plugincrafters.opwatch.storage;

public interface DataConnection<T> {

    T getConnection();
    void connect();
    void disconnect();
}
