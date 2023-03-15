package team.plugincrafters.opwatch.storage.repositories;

import java.util.Set;

public interface ObjectRepository<T, I> {

    T load(I id);
    Set<T> loadAll();
    void save(T t);
    void delete(I id);
    void start();
}