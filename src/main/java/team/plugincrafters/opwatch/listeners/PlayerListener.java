package team.plugincrafters.opwatch.listeners;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import team.plugincrafters.opwatch.managers.FileManager;
import team.plugincrafters.opwatch.utils.Utils;

import javax.inject.Inject;

public class PlayerListener implements Listener {

    @Inject
    private JavaPlugin plugin;
    @Inject
    private FileManager fileManager;
    public void start(){
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (player.isOp() && !isPlayerOnList(player.getName())){
            PlayerLoginEvent.Result result = punishPlayer(player);
            if (result.equals(PlayerLoginEvent.Result.ALLOWED)) return;
            FileConfiguration langFile = fileManager.get("language");

            event.disallow(result, Utils.format(fileManager.get("config"), langFile.getString("not-op")));
        }
    }


    private PlayerLoginEvent.Result punishPlayer(Player player){
        FileConfiguration config = fileManager.get("config");
        player.setOp(false);
        FileConfiguration langFile = fileManager.get("language");
        String message;
        PlayerLoginEvent.Result result = PlayerLoginEvent.Result.ALLOWED;

        if (config.getBoolean("ban-player")){
            String banReason = Utils.format(config, langFile.getString("not-op"));
            Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), banReason, null, null);
            message = Utils.format(config, langFile.getString("player-banned"));
            result = PlayerLoginEvent.Result.KICK_BANNED;
        } else if (config.getBoolean("kick-player")){
            message = Utils.format(config, langFile.getString("player-kicked"));
            result = PlayerLoginEvent.Result.KICK_OTHER;
        } else{
            message = Utils.format(config, langFile.getString("player-deop"));
        }

        Bukkit.getConsoleSender().sendMessage(message.replace("%player%", player.getName()));
        return result;
    }

    protected boolean isPlayerOnList(String playerName){
        return fileManager.get("opList").getStringList("op-list").contains(playerName);
    }
}
