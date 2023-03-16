package team.plugincrafters.opwatch.modules;

import org.bukkit.plugin.java.JavaPlugin;
import team.plugincrafters.opwatch.OpWatchPlugin;
import team.plugincrafters.opwatch.listeners.BlockEvents;
import team.plugincrafters.opwatch.listeners.CommandListener;
import team.plugincrafters.opwatch.listeners.PlayerListener;
import team.plugincrafters.opwatch.managers.FileManager;
import team.plugincrafters.opwatch.managers.PunishmentManager;
import team.plugincrafters.opwatch.managers.TwoAuthFactorManager;
import team.plugincrafters.opwatch.managers.UserManager;
import team.plugincrafters.opwatch.utils.QRMap;
import team.unnamed.inject.Binder;
import team.unnamed.inject.Module;

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
        binder.bind(BlockEvents.class).singleton();
        binder.bind(UserManager.class).singleton();
        binder.bind(FileManager.class).singleton();
        binder.bind(PunishmentManager.class).singleton();
        binder.bind(TwoAuthFactorManager.class).singleton();
        binder.bind(QRMap.class).singleton();

        binder.install(new CommandsModule());
        binder.install(new StorageModule(plugin));
    }
}