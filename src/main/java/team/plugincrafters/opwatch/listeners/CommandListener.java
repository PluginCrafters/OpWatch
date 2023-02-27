package team.plugincrafters.opwatch.listeners;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;
import team.plugincrafters.opwatch.managers.FileManager;
import team.plugincrafters.opwatch.utils.Utils;

import javax.inject.Inject;

public class CommandListener implements Listener {

    @Inject
    private JavaPlugin plugin;
    @Inject
    private FileManager fileManager;

    public void start(){
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (!checkOpPermissions(event.getPlayer(), event.getMessage())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerCommand(ServerCommandEvent event) {
        if (!checkOpPermissions(event.getSender(), event.getCommand())) event.setCancelled(true);
    }

    private boolean checkOpPermissions(CommandSender sender, String command) {
        if (command.split(" ").length <= 1 || !sender.hasPermission("minecraft.command.op")
                || (!command.startsWith("op ") && !command.startsWith("/op ")
                && !command.startsWith("minecraft:op ") && !command.startsWith("/minecraft:op "))) return true;

        String playerName = command.split(" ")[1];
        if (isPlayerOnList(playerName)) return true;

        sender.sendMessage(Utils.format(fileManager.get("config"),
                fileManager.get("language").getString("no-permission-for-op").replace("%player%", playerName)));
        return false;
    }

    private boolean isPlayerOnList(String playerName){
        return fileManager.get("opList").getStringList("op-list").contains(playerName);
    }
}
