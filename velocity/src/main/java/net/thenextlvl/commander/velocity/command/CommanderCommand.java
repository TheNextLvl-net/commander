package net.thenextlvl.commander.velocity.command;

import com.velocitypowered.api.command.BrigadierCommand;
import net.thenextlvl.commander.velocity.CommanderPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CommanderCommand {

    public BrigadierCommand create(CommanderPlugin plugin) {
        var command = BrigadierCommand.literalArgumentBuilder("commandv")
                .requires(source -> source.hasPermission("commander.admin"))
                .then(new HideCommand(plugin).create())
                .then(new PermissionCommand(plugin).create())
                .then(new RegisterCommand(plugin).create())
                .then(new ReloadCommand(plugin).create())
                .then(new ResetCommand(plugin).create())
                .then(new RevealCommand(plugin).create())
                .then(new SaveCommand(plugin).create())
                .then(new UnregisterCommand(plugin).create())
                .build();
        return new BrigadierCommand(command);
    }
}
