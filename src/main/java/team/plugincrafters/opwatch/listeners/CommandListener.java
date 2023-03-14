package team.plugincrafters.opwatch.listeners;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import team.plugincrafters.opwatch.OpWatchPlugin;
import team.plugincrafters.opwatch.managers.FileManager;
import team.plugincrafters.opwatch.managers.PunishmentManager;
import team.plugincrafters.opwatch.managers.TwoAuthFactorManager;
import team.plugincrafters.opwatch.utils.Utils;

import javax.inject.Inject;
import java.util.List;

public class CommandListener implements Listener {

    @Inject
    private OpWatchPlugin plugin;
    @Inject
    private FileManager fileManager;
    @Inject
    private PunishmentManager punishmentManager;
    @Inject
    private TwoAuthFactorManager twoAuthFactorManager;

    public void start(){
        Bukkit.getPluginManager().registerEvents(this, plugin);

        LuckPerms luckPerms = plugin.getLuckperms();
        if (luckPerms == null || !fileManager.get("config").getBoolean("punishment.check-luckperms")) return;

        EventBus eventBus = luckPerms.getEventBus();
        eventBus.subscribe(plugin, NodeAddEvent.class, this::onLuckpermsCommand);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (fileManager.get("config").getBoolean("auth.enabled") && !twoAuthFactorManager.playerIsAuthenticated(player)){
            event.setCancelled(true);
            return;
        }

        if (senderHasIrregularities(player, event.getMessage())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerCommand(ServerCommandEvent event) {
        if (senderHasIrregularities(event.getSender(), event.getCommand())) event.setCancelled(true);
    }


    private void onLuckpermsCommand(NodeAddEvent event){
        if (!event.isUser() || !(event.getNode() instanceof PermissionNode)) return;

        PermissionNode node = (PermissionNode) event.getNode();
        List<String> permissions = fileManager.get("config").getStringList("punishment.permissions-list");
        if (!permissions.contains(node.getPermission())) return;

        Player player = Bukkit.getPlayer(((User) event.getTarget()).getUniqueId());
        if (player == null) return;
        String playerName = player.getName();
        if (playerName == null || isPlayerOnList(playerName)) return;

        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
            Bukkit.getConsoleSender().sendMessage(Utils.format(fileManager.get("config"),
                    fileManager.get("language").getString("no-permission-for-op-list").replace("%player%", playerName)));

            punishmentManager.removePermissions(Bukkit.getPlayerExact(playerName));
        });
    }

    private boolean senderHasIrregularities(CommandSender sender, String command) {
        List<String> opCommands = fileManager.get("config").getStringList("punishment.op-commands");
        if (opCommands == null) return false;

        for (String opCommand : opCommands) {
            if (!opCommand.contains("<player>")
                    || !command.replace("/", "")
                    .matches(opCommand.split(" :: ")[0].replace("<player>", "\\S+").replace("/", ""))) continue;

            String perm = "";
            if (opCommand.split(" :: ").length == 2) perm = opCommand.split(" :: ")[1];
            FileConfiguration langFile = fileManager.get("language");

            if (!sender.hasPermission(perm.replaceAll(" ", ""))){
                sender.sendMessage(Utils.format(fileManager.get("config"),
                        langFile.getString("no-permission-for-op-permission")).replace("%perm%", perm));
                return true;
            }

            String[] splitCommand = command.split(" ");
            int playerPos = opCommand.indexOf("<player>");
            String playerName = splitCommand[opCommand.substring(0, playerPos).split(" ").length];

            if (isPlayerOnList(playerName)) return false;

            sender.sendMessage(Utils.format(fileManager.get("config"),
                    langFile.getString("no-permission-for-op-list").replace("%player%", playerName)));
            return true;
        }
        return false;
    }

    private boolean isPlayerOnList(String playerName){
        return fileManager.get("opList").getStringList("op-list").contains(playerName);
    }
}