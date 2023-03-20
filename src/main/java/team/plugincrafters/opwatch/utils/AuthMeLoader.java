package team.plugincrafters.opwatch.utils;

import fr.xephi.authme.api.v3.AuthMeApi;
import fr.xephi.authme.events.LoginEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import team.plugincrafters.opwatch.managers.FileManager;
import team.plugincrafters.opwatch.managers.PunishmentManager;
import team.plugincrafters.opwatch.managers.TwoAuthFactorManager;

public class AuthMeLoader implements Listener {

    private final AuthMeApi authMeApi;
    private final PunishmentManager punishmentManager;
    private final FileManager fileManager;
    private final TwoAuthFactorManager twoAuthFactorManager;

    public AuthMeLoader(JavaPlugin plugin, PunishmentManager punishmentManager, FileManager fileManager, TwoAuthFactorManager twoAuthFactorManager){
        this.authMeApi = AuthMeApi.getInstance();
        this.punishmentManager = punishmentManager;
        this.fileManager = fileManager;
        this.twoAuthFactorManager = twoAuthFactorManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onAuthLogin(LoginEvent event){
        Player player = event.getPlayer();
        if (!punishmentManager.isPowerful(player)) return;

        if (!fileManager.get("config").getBoolean("auth.enabled")) return;
        twoAuthFactorManager.joinPlayer(player);
    }

    public boolean isAuthMeWaitingPlayer(Player player){
        return authMeApi != null && !authMeApi.isAuthenticated(player);
    }

}
