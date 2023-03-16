package team.plugincrafters.opwatch;

import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;
import com.jeff_media.updatechecker.UserAgentBuilder;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import net.luckperms.api.LuckPerms;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import team.plugincrafters.opwatch.commands.MainCommand;
import team.plugincrafters.opwatch.listeners.BlockEvents;
import team.plugincrafters.opwatch.listeners.CommandListener;
import team.plugincrafters.opwatch.listeners.PlayerListener;
import team.plugincrafters.opwatch.managers.FileManager;
import team.plugincrafters.opwatch.managers.PunishmentManager;
import team.plugincrafters.opwatch.managers.UserManager;
import team.plugincrafters.opwatch.modules.CoreModule;
import team.plugincrafters.opwatch.storage.connections.DataConnection;
import team.unnamed.inject.Injector;

import javax.inject.Inject;
import java.sql.Connection;

public class OpWatchPlugin extends JavaPlugin {

    @Inject
    private DataConnection<Connection> connection;
    @Inject
    private FileManager fileManager;
    @Inject
    private PlayerListener playerListener;
    @Inject
    private CommandListener commandListener;
    @Inject
    private BlockEvents blockEvents;
    @Inject
    private UserManager userManager;
    @Inject
    private MainCommand mainCommand;
    @Inject
    private PunishmentManager punishmentManager;

    private LuckPerms luckPerms;
    private GoogleAuthenticator gAuth;

    @Override
    public void onEnable() {
        try {
            Injector injector = Injector.create(new CoreModule(this));
            injector.injectMembers(this);
        } catch (Exception e){
            e.printStackTrace();
        }

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
        }
        start();
    }


    private void start(){
        fileManager.loadAllFileConfigurations();
        playerListener.start();
        commandListener.start();
        connection.connect();
        mainCommand.start();
        userManager.start();
        punishmentManager.start();

        reloadAuth();
        int pluginId = 17946;
        new Metrics(this, pluginId);
        checkUpdates();
    }

    private void checkUpdates(){
        if (!fileManager.get("config").getBoolean("update-checker")) return;
        final String id = "93347";
        new UpdateChecker(this, UpdateCheckSource.SPIGOT, id)
                .setDownloadLink(id)
                .setDonationLink("https://paypal.me/RosenM00?country.x=AR&locale.x=es_XC")
                .setChangelogLink(id)
                .setNotifyOpsOnJoin(true)
                .setNotifyByPermissionOnJoin("opwatch.updatechecker")
                .setUserAgent(new UserAgentBuilder().addPluginNameAndVersion())
                .checkEveryXHours(3)
                .checkNow();
    }

    public void reloadAuth(){
        if (fileManager.get("config").getBoolean("auth.enabled")) blockEvents.start();

        if (gAuth == null) gAuth = new GoogleAuthenticator();
    }

    @Override
    public void onDisable(){
        userManager.saveAll();
        connection.disconnect();
    }

    public LuckPerms getLuckperms(){
        return luckPerms;
    }

    public GoogleAuthenticator getgAuth() {
        return gAuth;
    }
}
