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
import java.util.List;

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
        if (senderHasIrregularities(event.getPlayer(), event.getMessage())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerCommand(ServerCommandEvent event) {
        if (senderHasIrregularities(event.getSender(), event.getCommand())) event.setCancelled(true);
    }

    private boolean senderHasIrregularities(CommandSender sender, String command) {
        List<String> opCommands = fileManager.get("config").getStringList("op-commands");
        if (opCommands == null) return false;

        for (String opCommand : opCommands) {
            if (!opCommand.contains("<player>")
                    || !command.replace("/", "")
                    .matches(opCommand.split(" :: ")[0].replace("<player>", "\\S+").replace("/", ""))) continue;

            String perm = "";
            if (opCommand.split("::").length == 2) perm = opCommand.split("::")[1];
            if (!sender.hasPermission(perm.replaceAll(" ", ""))) return false;

            String[] splitCommand = command.split(" ");
            int playerPos = opCommand.indexOf("<player>");
            String playerName = splitCommand[opCommand.substring(0, playerPos).split(" ").length];
            if (isPlayerOnList(playerName)) return false;

            sender.sendMessage(Utils.format(fileManager.get("config"),
                    fileManager.get("language").getString("no-permission-for-op").replace("%player%", playerName)));
            return true;
        }
        return false;
    }

    /*private boolean senderHasIrregularities(CommandSender sender, String command) {
        if (command.split(" ").length <= 1 || !sender.hasPermission("minecraft.command.op")
                || (!command.startsWith("op ") && !command.startsWith("/op ")
                && !command.startsWith("minecraft:op ") && !command.startsWith("/minecraft:op "))) return false;

        String playerName = command.split(" ")[1];
        if (isPlayerOnList(playerName)) return false;


        sender.sendMessage(Utils.format(fileManager.get("config"),
                fileManager.get("language").getString("no-permission-for-op").replace("%player%", playerName)));
        return true;
    }*/

    private boolean isPlayerOnList(String playerName){
        return fileManager.get("opList").getStringList("op-list").contains(playerName);
    }
}
