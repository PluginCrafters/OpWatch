package team.plugincrafters.opwatch.modules;

import me.yushust.inject.Binder;
import me.yushust.inject.Module;
import org.bukkit.plugin.java.JavaPlugin;
import team.plugincrafters.opwatch.OpWatchPlugin;

public class CoreModule implements Module {

    private final OpWatchPlugin plugin;

    public CoreModule(OpWatchPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void configure(Binder binder){
        binder.bind(JavaPlugin.class).toInstance(plugin);
        binder.bind(OpWatchPlugin.class).toInstance(plugin);
    }
}