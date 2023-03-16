package team.plugincrafters.opwatch.modules;

import team.plugincrafters.opwatch.OpWatchPlugin;
import team.plugincrafters.opwatch.storage.connections.DataConnection;
import team.plugincrafters.opwatch.storage.connections.H2Connection;
import team.plugincrafters.opwatch.storage.connections.HikariConnection;
import team.plugincrafters.opwatch.storage.repositories.ObjectRepository;
import team.plugincrafters.opwatch.storage.repositories.OpWatchSQLRepository;
import team.plugincrafters.opwatch.users.User;
import team.unnamed.inject.Binder;
import team.unnamed.inject.Module;
import team.unnamed.inject.key.TypeReference;

import java.sql.Connection;

public class StorageModule implements Module {

    private final OpWatchPlugin plugin;

    public StorageModule(OpWatchPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void configure(Binder binder){
        String storageType = plugin.getConfig().getString("storage.type");
        storageType = storageType == null ? "h2" : storageType;

        if (storageType.equalsIgnoreCase("MySQL")){
            binder.bind(new TypeReference<DataConnection<Connection>>(){}).to(HikariConnection.class).singleton();
        } else{
            binder.bind(new TypeReference<DataConnection<Connection>>(){}).to(H2Connection.class).singleton();
        }

        binder.bind(new TypeReference<ObjectRepository<User, String>>(){})
                .to(OpWatchSQLRepository.class).singleton();
    }
}