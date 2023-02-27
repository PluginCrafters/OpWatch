package team.plugincrafters.opwatch.modules;

import me.yushust.inject.Binder;
import me.yushust.inject.Module;
import me.yushust.inject.key.TypeReference;
import org.bukkit.plugin.java.JavaPlugin;
import team.plugincrafters.opwatch.OpWatchPlugin;
import team.plugincrafters.opwatch.User;
import team.plugincrafters.opwatch.listeners.CommandListener;
import team.plugincrafters.opwatch.listeners.PlayerListener;
import team.plugincrafters.opwatch.managers.FileManager;
import team.plugincrafters.opwatch.managers.UserManager;
import team.plugincrafters.opwatch.storage.DataConnection;
import team.plugincrafters.opwatch.storage.H2Connection;
import team.plugincrafters.opwatch.storage.repositories.ObjectRepository;
import team.plugincrafters.opwatch.storage.repositories.OpWatchSQLRepository;

import java.sql.Connection;

public class CoreModule implements Module {

    private final OpWatchPlugin plugin;

    public CoreModule(OpWatchPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void configure(Binder binder){
        binder.bind(JavaPlugin.class).toInstance(plugin);
        binder.bind(OpWatchPlugin.class).toInstance(plugin);

        binder.bind(PlayerListener.class).singleton();
        binder.bind(CommandListener.class).singleton();
        binder.bind(UserManager.class).singleton();
        binder.bind(FileManager.class).singleton();

        binder.install(new CommandsModule());

        binder.bind(new TypeReference<DataConnection<Connection>>(){}).to(H2Connection.class).singleton();
        binder.bind(new TypeReference<ObjectRepository<User, String>>(){}).to(OpWatchSQLRepository.class).singleton();
    }
}