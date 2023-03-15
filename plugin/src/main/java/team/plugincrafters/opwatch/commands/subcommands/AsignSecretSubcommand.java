package team.plugincrafters.opwatch.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import team.plugincrafters.opwatch.managers.FileManager;
import team.plugincrafters.opwatch.managers.UserManager;
import team.plugincrafters.opwatch.users.User;
import team.plugincrafters.opwatch.utils.Utils;

import javax.inject.Inject;

public class AsignSecretSubcommand extends Subcommand{

    @Inject
    private FileManager fileManager;
    @Inject
    private UserManager userManager;

    protected AsignSecretSubcommand(){
        super("reassign","opwatch.reassign", 2);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        User user = userManager.getUserByName(args[0]);
        FileConfiguration langFile = fileManager.get("language");
        FileConfiguration configFile = fileManager.get("config");
        if (user == null){
            sender.sendMessage(Utils.format(configFile, langFile.getString("user-not-found")));
            return true;
        }

        userManager.removeUser(user);
        sender.sendMessage(Utils.format(configFile, langFile.getString("qr-code-changed")
                .replace("%player%", user.getName())));
        return true;
    }
}
