package team.plugincrafters.opwatch.modules;


import team.plugincrafters.opwatch.commands.MainCommand;
import team.plugincrafters.opwatch.commands.subcommands.AsignSecretSubcommand;
import team.plugincrafters.opwatch.commands.subcommands.HelpSubcommand;
import team.plugincrafters.opwatch.commands.subcommands.ReloadSubcommand;
import team.unnamed.inject.Binder;
import team.unnamed.inject.Module;

public class CommandsModule implements Module {

    @Override
    public void configure(Binder binder){
        binder.bind(MainCommand.class).singleton();
        binder.bind(ReloadSubcommand.class).singleton();
        binder.bind(HelpSubcommand.class).singleton();
        binder.bind(AsignSecretSubcommand.class).singleton();
    }

}
