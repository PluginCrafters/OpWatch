package team.plugincrafters.opwatch.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import team.plugincrafters.opwatch.managers.FileManager;

import javax.inject.Inject;

public class CommonListener implements Listener {

    @Inject
    private JavaPlugin plugin;
    @Inject
    private FileManager fileManager;

    public void start(){
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    protected boolean isPlayerOnList(String playerName){
        return fileManager.get("opList").getStringList("op-list").contains(playerName);
    }

    public FileManager getFileManager() {
        return fileManager;
    }
}
