package team.plugincrafters.opwatch;

import me.yushust.inject.Injector;
import org.bukkit.plugin.java.JavaPlugin;
import team.plugincrafters.opwatch.modules.CoreModule;

public class OpWatchPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        try {
            Injector injector = Injector.create(new CoreModule(this));
            injector.injectMembers(this);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
