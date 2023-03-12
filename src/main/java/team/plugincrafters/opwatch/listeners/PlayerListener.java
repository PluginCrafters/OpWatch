package team.plugincrafters.opwatch.listeners;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import team.plugincrafters.opwatch.managers.FileManager;
import team.plugincrafters.opwatch.managers.PunishmentManager;
import team.plugincrafters.opwatch.utils.Utils;

import javax.inject.Inject;

public class PlayerListener implements Listener {

    @Inject
    private JavaPlugin plugin;
    @Inject
    private FileManager fileManager;
    @Inject
    private PunishmentManager punishmentManager;

    public void start(){
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (punishmentManager.isPowerful(player) && !isPlayerOnList(player.getName())){
            PlayerLoginEvent.Result result = punishmentManager.punishPlayer(player);
            if (result.equals(PlayerLoginEvent.Result.ALLOWED)) return;
            FileConfiguration langFile = fileManager.get("language");

            event.disallow(result, Utils.format(fileManager.get("config"), langFile.getString("not-op")));
        }
    }


    protected boolean isPlayerOnList(String playerName){
        return fileManager.get("opList").getStringList("op-list").contains(playerName);
    }
}
