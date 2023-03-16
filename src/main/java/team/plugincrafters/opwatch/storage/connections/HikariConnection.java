package team.plugincrafters.opwatch.storage.connections;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;

public class HikariConnection implements DataConnection<Connection> {

    private HikariDataSource dataSource;

    @Inject
    private JavaPlugin plugin;
    private String ip, user, password, database;

    private final String OPWATCH_TABLE = "CREATE TABLE IF NOT EXISTS `opwatch_table` (`uuid` varchar(36) NOT NULL, " +
            "`player_name` VARCHAR (16) NOT NULL, " +
            "`ip` VARCHAR(15) NOT NULL, " +
            "`secret_code` VARCHAR(50) NOT NULL, " +
            "PRIMARY KEY (`uuid`));";

    @Override
    public Connection getConnection(){
        try{
            Connection connection = dataSource.getConnection();
            if (connection.isClosed()){
                this.refreshConnection();
                return dataSource.getConnection();
            }
            return connection;
        } catch (SQLException exception){
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public void connect(){
        ConfigurationSection config = plugin.getConfig();
        this.ip = config.getString("storage.ip") + ":" + config.getInt("storage.port");
        if (ip.contains("localhost")){
            ip = ip.replace("localhost", "127.0.0.1");
        }
        this.user = config.getString("storage.username");
        this.password =  config.getString("storage.password");
        this.database = config.getString("storage.database");

        this.setConnectionFields();
        try {
            Connection connection = dataSource.getConnection();
            connection.createStatement().executeUpdate(OPWATCH_TABLE);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private synchronized void setConnectionFields() {
        dataSource = new HikariDataSource();
        dataSource.setPoolName("OpWatch - Connection pool");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://" + ip + "/" + database + "?characterEncoding=utf8&allowPublicKeyRetrieval=true");
        dataSource.addDataSourceProperty("cachePrepStmts", "true");
        dataSource.addDataSourceProperty("prepStmtCacheSize", "250");
        dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        dataSource.addDataSourceProperty("characterEncoding", "utf8");
        dataSource.addDataSourceProperty("encoding", "UTF-8");
        dataSource.addDataSourceProperty("useUnicode", "true");
        dataSource.addDataSourceProperty("useSSL", "false");
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setMinimumIdle(10);
        dataSource.setMaximumPoolSize(10);
        dataSource.setMaxLifetime(600000L);
        dataSource.setMinimumIdle(5);
        dataSource.setConnectionTimeout(9999999999L);
        dataSource.setMaximumPoolSize(15);
    }

    @Override
    public void disconnect(){
        try{
            Connection connection = dataSource.getConnection();
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void refreshConnection() {
        if(dataSource != null) {
            dataSource.close();
        }
        this.setConnectionFields();
        try {
            Connection connection = dataSource.getConnection();
            if(connection == null || connection.isClosed())
                connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}