package team.plugincrafters.opwatch.storage.repositories;

import team.plugincrafters.opwatch.users.User;

import java.util.List;

public class OpWatchSQLRepository implements ObjectRepository<User, String>{

    @Override
    public User load(String id) {
        return null;
    }

    @Override
    public List<User> loadAll() {
        return null;
    }

    @Override
    public void save(User user) {

    }

    @Override
    public void delete(String id) {

    }

    @Override
    public void start() {

    }
}
