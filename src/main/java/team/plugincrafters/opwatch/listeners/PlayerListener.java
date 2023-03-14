package team.plugincrafters.opwatch.listeners;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import team.plugincrafters.opwatch.managers.TwoAuthFactorManager;
import team.plugincrafters.opwatch.users.User;
import team.plugincrafters.opwatch.managers.FileManager;
import team.plugincrafters.opwatch.managers.PunishmentManager;
import team.plugincrafters.opwatch.managers.UserManager;
import team.plugincrafters.opwatch.users.UserState;
import team.plugincrafters.opwatch.utils.Utils;

import javax.inject.Inject;

public class PlayerListener implements Listener {

    @Inject
    private JavaPlugin plugin;
    @Inject
    private FileManager fileManager;
    @Inject
    private PunishmentManager punishmentManager;
    @Inject
    private TwoAuthFactorManager twoAuthFactorManager;

    public void start(){
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (!punishmentManager.isPowerful(player)) return;
        FileConfiguration langFile = fileManager.get("language");

        if (!isPlayerOnList(player.getName())){
            PlayerLoginEvent.Result result = punishmentManager.punishPlayer(player);
            if (!result.equals(PlayerLoginEvent.Result.ALLOWED)){
                event.disallow(result, Utils.format(fileManager.get("config"), langFile.getString("not-op")));
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if (!punishmentManager.isPowerful(player)) return;

        twoAuthFactorManager.joinPlayer(player);
    }

    private boolean isPlayerOnList(String playerName){
        return fileManager.get("opList").getStringList("op-list").contains(playerName);
    }
}