package team.plugincrafters.opwatch;

import me.yushust.inject.Injector;
import org.bukkit.plugin.java.JavaPlugin;
import team.plugincrafters.opwatch.listeners.CommonListener;
import team.plugincrafters.opwatch.managers.FileManager;
import team.plugincrafters.opwatch.managers.UserManager;
import team.plugincrafters.opwatch.modules.CoreModule;
import team.plugincrafters.opwatch.storage.DataConnection;

import javax.inject.Inject;
import java.sql.Connection;

public class OpWatchPlugin extends JavaPlugin {

    @Inject
    private DataConnection<Connection> connection;
    @Inject
    private FileManager fileManager;
    @Inject
    private CommonListener commonListener;
    @Inject
    private UserManager userManager;

    @Override
    public void onEnable() {
        try {
            Injector injector = Injector.create(new CoreModule(this));
            injector.injectMembers(this);
        } catch (Exception e){
            e.printStackTrace();
        }

        commonListener.start();
        connection.connect();

        userManager.start();
        fileManager.loadAllFileConfigurations();
    }

    @Override
    public void onDisable(){
        connection.disconnect();
    }
}
