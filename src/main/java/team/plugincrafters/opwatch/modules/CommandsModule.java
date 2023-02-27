package team.plugincrafters.opwatch.modules;

import me.yushust.inject.Binder;
import me.yushust.inject.Module;
import team.plugincrafters.opwatch.commands.MainCommand;
import team.plugincrafters.opwatch.commands.subcommands.HelpSubcommand;
import team.plugincrafters.opwatch.commands.subcommands.ReloadSubcommand;

public class CommandsModule implements Module {

    @Override
    public void configure(Binder binder){
        binder.bind(MainCommand.class).singleton();
        binder.bind(ReloadSubcommand.class).singleton();
        binder.bind(HelpSubcommand.class).singleton();
    }

}
