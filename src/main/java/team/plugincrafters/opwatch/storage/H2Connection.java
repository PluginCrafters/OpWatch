package team.plugincrafters.opwatch.storage;

import org.h2.jdbcx.JdbcConnectionPool;
import team.plugincrafters.opwatch.OpWatchPlugin;

import javax.inject.Inject;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class H2Connection implements DataConnection<Connection> {

    @Inject
    private OpWatchPlugin plugin;

    private JdbcConnectionPool connectionPool;

    private final String REPORT_TABLE = "CREATE TABLE IF NOT EXISTS `opwatch_table` (`uuid` varchar(36) NOT NULL, " +
            "`player_name` VARCHAR (16) NOT NULL, " +
            "`ip` VARCHAR(15) NOT NULL, " +
            "PRIMARY KEY (`uuid`));";

    @Override
    public Connection getConnection(){
        try {
            return connectionPool.getConnection();
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public void connect(){
        File file = new File(plugin.getDataFolder(), "h2-file.db");
        connectionPool = JdbcConnectionPool.create("jdbc:h2:file:" + file.getAbsolutePath(), "", "");

        try {
            Connection connection = connectionPool.getConnection();
            connection.createStatement().executeUpdate(REPORT_TABLE);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void disconnect(){
        connectionPool.dispose();
    }

}