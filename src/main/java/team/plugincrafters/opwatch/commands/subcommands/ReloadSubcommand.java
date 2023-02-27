package team.plugincrafters.opwatch.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import team.plugincrafters.opwatch.managers.FileManager;
import team.plugincrafters.opwatch.utils.Utils;

import javax.inject.Inject;

public class ReloadSubcommand extends Subcommand{

    @Inject
    private JavaPlugin plugin;
    @Inject
    private FileManager fileManager;

    protected ReloadSubcommand() {
        super("reload","opwatch.reload", 1);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        FileConfiguration languageConfig = fileManager.get("language");
        plugin.reloadConfig();
        fileManager.loadAllFileConfigurations();
        sender.sendMessage(Utils.format(fileManager.get("config"), languageConfig.getString("reload-config")));
        return true;
    }
}
