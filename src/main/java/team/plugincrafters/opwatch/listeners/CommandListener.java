package team.plugincrafters.opwatch.listeners;

import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import team.plugincrafters.opwatch.managers.FileManager;
import team.plugincrafters.opwatch.utils.Utils;

public class CommandListener extends CommonListener{

    private final FileManager fileManager = getFileManager();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (!checkOpPermissions(event.getPlayer(), event.getMessage())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerCommand(ServerCommandEvent event) {
        if (!checkOpPermissions(event.getSender(), event.getCommand())) event.setCancelled(true);
    }

    private boolean checkOpPermissions(CommandSender sender, String command) {
        if (!command.startsWith("/op ")) return true;

        String playerName = command.split(" ")[1];
        if (isPlayerOnList(playerName)) return true;

        sender.sendMessage(Utils.format(fileManager.get("config"), fileManager.get("language").getString("no-permission-for-op")));
        return false;
    }
}
