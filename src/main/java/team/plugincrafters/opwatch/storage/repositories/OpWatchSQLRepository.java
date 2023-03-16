package team.plugincrafters.opwatch.storage.repositories;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import team.plugincrafters.opwatch.storage.connections.DataConnection;
import team.plugincrafters.opwatch.users.User;
import team.plugincrafters.opwatch.users.UserState;

import javax.inject.Inject;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class OpWatchSQLRepository implements ObjectRepository<User, String>{

    private final String SELECT_USER_UUID = "SELECT * FROM `opwatch_table` WHERE uuid=?;";
    private final String SELECT_ALL_USERS = "SELECT * FROM `opwatch_table`";
    private final String INSERT_USER = "INSERT INTO `opwatch_table` (uuid, player_name, ip, secret_code) VALUES (?, ?, ?, ?);";
    private final String UPDATE_USER = "UPDATE `opwatch_table` SET ip=? WHERE uuid=?;";
    private final String DELETE_USER = "DELETE FROM `opwatch_table` WHERE uuid=?;";


    @Inject
    private DataConnection<Connection> dataConnection;
    @Inject
    private JavaPlugin plugin;
    private Connection connection;

    @Override
    public void start(){
        connection = dataConnection.getConnection();
    }

    private Connection getConnection(){
        if (connection == null) connection = dataConnection.getConnection();
        return connection;
    }

    @Override
    public User load(String uuid) {
        User user = null;
        try{
            PreparedStatement statement = getConnection().prepareStatement(SELECT_USER_UUID);
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                String playerName = resultSet.getString("player_name");
                String ip = resultSet.getString("ip");
                String secret = resultSet.getString("secret_code");
                user = new User(UUID.fromString(uuid), playerName, ip, UserState.WAITING_CONFIRMATION, secret);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e){
            e.printStackTrace();
        }

        return user;
    }

    @Override
    public Set<User> loadAll() {
        Set<User> userList = new HashSet<>();

        try {
            Statement statement = getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(SELECT_ALL_USERS);
            while (resultSet.next()) {
                String uuid = resultSet.getString("uuid");
                String playerName = resultSet.getString("player_name");
                String ip = resultSet.getString("ip");
                String secret = resultSet.getString("secret_code");
                User user = new User(UUID.fromString(uuid), playerName, ip, UserState.WAITING_CONFIRMATION, secret);
                userList.add(user);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }

    @Override
    public void saveAsync(User user) {
        if (user == null) return;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> save(user));
    }

    private void save(User user){
        try {
            PreparedStatement statement = getConnection().prepareStatement(SELECT_USER_UUID);
            statement.setString(1, String.valueOf(user.getUuid()));
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()){
                statement = getConnection().prepareStatement(INSERT_USER);
                statement.setString(1, String.valueOf(user.getUuid()));
                statement.setString(2, user.getName());
                statement.setString(3, user.getIp());
                statement.setString(4, user.getSecret());
            } else{
                statement = getConnection().prepareStatement(UPDATE_USER);
                statement.setString(1, user.getIp());
                statement.setString(2, String.valueOf(user.getUuid()));
            }
            statement.executeUpdate();
            resultSet.close();
            statement.close();

        } catch (SQLException e){
            e.printStackTrace();
        }

    }

    @Override
    public void saveAll(Set<User> users){
        users.stream().filter(user -> user.getUserState().equals(UserState.LOGGED_IN)).forEach(this::save);
    }

    @Override
    public void delete(String uuid) {
        try {
            PreparedStatement statement = getConnection().prepareStatement(DELETE_USER);
            statement.setString(1, uuid);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
