package team.plugincrafters.opwatch.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import team.plugincrafters.opwatch.managers.FileManager;
import team.plugincrafters.opwatch.utils.Utils;

import javax.inject.Inject;

public class HelpSubcommand extends Subcommand{

    @Inject
    private JavaPlugin plugin;
    @Inject
    private FileManager fileManager;

    protected HelpSubcommand(){
        super("help","opwatch.help", 1);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args){
        FileConfiguration languageConfig = fileManager.get("language");
        sender.sendMessage(Utils.format(fileManager.get("config"), languageConfig.getString("help"))
                .replace("%author%", plugin.getDescription().getAuthors().get(0)));
        return true;
    }
}