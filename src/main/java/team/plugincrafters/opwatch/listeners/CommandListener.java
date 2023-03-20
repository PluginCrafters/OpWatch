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
        eventBus.subscribe(plugin, NodeAddEvent.class, this::onLuckPermsCommand);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (senderHasIrregularities(event.getPlayer(), event.getMessage())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerCommand(ServerCommandEvent event) {
        if (senderHasIrregularities(event.getSender(), event.getCommand())) event.setCancelled(true);
    }


    private void onLuckPermsCommand(NodeAddEvent event){
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
        String opCommand = this.matchCommand(command);
        if (opCommand == null) return false;

        String perm = opCommand.split(" :: ").length == 2 ? opCommand.split(" :: ")[1] : "";

        FileConfiguration langFile = fileManager.get("language");
        if (!sender.hasPermission(perm.replaceAll(" ", ""))){
            sender.sendMessage(Utils.format(fileManager.get("config"),
                    langFile.getString("no-permission-for-op-permission")).replace("%perm%", perm));
            return true;
        }

        String playerName = this.getPlayerName(command, opCommand);
        if (playerName == null) return false;

        if (isPlayerOnList(playerName)){
            if (fileManager.get("config").getBoolean("auth.enabled")){
                Player player = Bukkit.getPlayerExact(playerName);
                if (player != null) twoAuthFactorManager.joinPlayer(player);
            }

            return false;
        }

        sender.sendMessage(Utils.format(fileManager.get("config"),
                langFile.getString("no-permission-for-op-list").replace("%player%", playerName)));
        return true;
    }

    private String getPlayerName(String command, String opCommand){
        String[] splitCommand = command.split(" ");
        int playerPos = opCommand.indexOf("<player>");

        return splitCommand[opCommand.substring(0, playerPos).split(" ").length];
    }

    private String matchCommand(String command){
        List<String> opCommands = fileManager.get("config").getStringList("punishment.op-commands");
        if (opCommands == null) return null;

        return opCommands.stream().filter(opCommand -> opCommand.contains("<player>")
                        && command.replace("/", "")
                        .matches(opCommand.split(" :: ")[0]
                                .replace("<player>", "\\S+")
                                .replace("/", "")))
                .findFirst().orElse(null);
    }

    private boolean isPlayerOnList(String playerName){
        return fileManager.get("opList").getStringList("op-list").contains(playerName);
    }
}