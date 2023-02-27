package team.plugincrafters.opwatch.storage.repositories;

import java.util.List;

public interface ObjectRepository<T, I> {

    T load(I id);
    List<T> loadAll();
    void save(T t);
    void delete(I id);
    void start();
}
