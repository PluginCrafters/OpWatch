package team.plugincrafters.opwatch.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import team.plugincrafters.opwatch.OpWatchPlugin;
import team.plugincrafters.opwatch.commands.subcommands.AsignSecretSubcommand;
import team.plugincrafters.opwatch.commands.subcommands.HelpSubcommand;
import team.plugincrafters.opwatch.commands.subcommands.ReloadSubcommand;
import team.plugincrafters.opwatch.commands.subcommands.Subcommand;
import team.plugincrafters.opwatch.managers.FileManager;
import team.plugincrafters.opwatch.managers.UserManager;
import team.plugincrafters.opwatch.users.User;
import team.plugincrafters.opwatch.utils.Utils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainCommand implements TabExecutor {

    @Inject
    private OpWatchPlugin plugin;
    @Inject
    private FileManager fileManager;
    @Inject
    private ReloadSubcommand reloadSubcommand;
    @Inject
    private HelpSubcommand helpSubcommand;
    @Inject
    private UserManager userManager;
    @Inject
    private AsignSecretSubcommand asignSecretSubcommand;


    private final List<Subcommand> subcommands = new ArrayList<>();

    public void start(){
        Objects.requireNonNull(plugin.getCommand("opwatch")).setExecutor(this);
        subcommands.add(reloadSubcommand);
        subcommands.add(helpSubcommand);
        subcommands.add(asignSecretSubcommand);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0){
            helpSubcommand.execute(sender, args);
            return true;
        }

        String name = args[0];
        Subcommand subCommand = subcommands.stream().filter(subC ->
                subC.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        if (subCommand == null || !subCommand.argsLengthMatches(args.length)) {
            helpSubcommand.execute(sender, args);
            return true;
        }

        if (!subCommand.hasPermission(sender)){
            FileConfiguration languageConfig = fileManager.get("language");
            sender.sendMessage(Utils.format(fileManager.get("config"), languageConfig.getString("no-permission")));
            return false;
        }

        if (!subCommand.execute(sender, Arrays.copyOfRange(args,1,args.length))){
            helpSubcommand.execute(sender, args);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        for (Subcommand subcommand : subcommands) {
            if (sender.hasPermission(subcommand.getPermission())){
                if (args.length == 1) list.add(subcommand.getName());
                if (args.length == 2 && subcommand.getName().equalsIgnoreCase("reassign")){
                    for (User user : userManager.getUsers()){
                        list.add(user.getName());
                    }
                }
            }
        }
        return list;
    }
}
