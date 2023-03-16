package team.plugincrafters.opwatch.storage.repositories;

import java.util.Set;

public interface ObjectRepository<T, I> {

    T load(I id);
    Set<T> loadAll();
    void saveAsync(T t);
    void saveAll(Set<T> list);
    void delete(I id);
    void start();
}