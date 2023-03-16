package team.plugincrafters.opwatch.managers;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scheduler.BukkitTask;
import team.plugincrafters.opwatch.OpWatchPlugin;
import team.plugincrafters.opwatch.utils.Utils;

import javax.inject.Inject;
import java.util.List;

public class PunishmentManager {

    @Inject
    private FileManager fileManager;
    @Inject
    private OpWatchPlugin plugin;
    private BukkitTask timerTask;

    public void start(){
        int interval = fileManager.get("config").getInt("op-check-interval") * 20;
        timerTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::checkOpPlayers, 0, interval);
    }

    public void reload(){
        timerTask.cancel();
        start();
    }

    public PlayerLoginEvent.Result punishPlayer(Player player){
        FileConfiguration config = fileManager.get("config");
        player.setOp(false);
        this.removePermissions(player);
        FileConfiguration langFile = fileManager.get("language");
        String message;
        PlayerLoginEvent.Result result = PlayerLoginEvent.Result.ALLOWED;

        String banReason = Utils.format(config, langFile.getString("not-op"));
        if (config.getBoolean("punishment.ban-player")){
            player.kickPlayer(banReason);
            Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), banReason, null, null);
            message = Utils.format(config, langFile.getString("player-banned"));
            result = PlayerLoginEvent.Result.KICK_BANNED;
        } else if (config.getBoolean("punishment.kick-player")){
            message = Utils.format(config, langFile.getString("player-kicked"));
            player.kickPlayer(banReason);
            result = PlayerLoginEvent.Result.KICK_OTHER;
        } else{
            message = Utils.format(config, langFile.getString("player-deop"));
            player.sendMessage(banReason);
        }

        Bukkit.getConsoleSender().sendMessage(message.replace("%player%", player.getName()));
        return result;
    }

    private void checkOpPlayers(){
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (this.isPowerful(player)) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (!fileManager.get("opList").getStringList("op-list").contains(player.getName())){
                       punishPlayer(player);
                    }
                });
            }
        });
    }

    public boolean isPowerful(Player player){
        if (player.isOp()) return true;
        List<String> permissions = fileManager.get("config").getStringList("punishment.permissions-list");

        if (permissions.stream().anyMatch(player::hasPermission)) return true;

        LuckPerms luckPerms = plugin.getLuckperms();
        if (luckPerms == null) return false;
        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
        return user.getNodes(NodeType.PERMISSION).stream().map(PermissionNode::getPermission).anyMatch(permissions::contains);
    }

    public void removePermissions(Player player){
        fileManager.get("config").getStringList("punishment.permissions-list").forEach(permission -> {
            PermissionAttachment attachment = player.addAttachment(plugin);
            attachment.unsetPermission(permission);
            LuckPerms luckPerms = plugin.getLuckperms();
            if (luckPerms == null) return;

            User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
            user.data().remove(Node.builder(permission).build());
            luckPerms.getUserManager().saveUser(user);
        });
    }
}
