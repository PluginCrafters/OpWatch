package team.plugincrafters.opwatch.listeners;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import team.plugincrafters.opwatch.managers.FileManager;
import team.plugincrafters.opwatch.utils.Utils;

public class PlayerListener extends CommonListener {

    private final FileManager fileManager = getFileManager();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.isOp() && !isPlayerOnList(player.getName())) punishPlayer(player);
    }


    private void punishPlayer(Player player){
        FileConfiguration config = fileManager.get("config");
        player.setOp(false);

        FileConfiguration langFile = fileManager.get("language");
        String banReason = Utils.format(config, langFile.getString("not-op"));
        String message;

        if (config.getBoolean("ban-player")){
            player.kickPlayer(banReason);
            Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), banReason, null, null);
            message = Utils.format(config, langFile.getString("player-banned"));
        } else if (config.getBoolean("kick-player")){
            player.kickPlayer(banReason);
            message = Utils.format(config, langFile.getString("player-kicked"));
        } else return;

        Bukkit.getConsoleSender().sendMessage(message.replace("%player%", player.getName()));
    }
}
