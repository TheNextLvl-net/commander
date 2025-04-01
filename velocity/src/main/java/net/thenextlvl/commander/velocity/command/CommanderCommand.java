package net.thenextlvl.commander.velocity.command;

import com.velocitypowered.api.command.BrigadierCommand;
import net.thenextlvl.commander.velocity.CommanderPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CommanderCommand {
    public static BrigadierCommand create(CommanderPlugin plugin) {
        var command = BrigadierCommand.literalArgumentBuilder("commandv")
                .requires(source -> source.hasPermission("commander.admin"))
                .then(HideCommand.create(plugin))
                .then(PermissionCommand.create(plugin))
                .then(RegisterCommand.create(plugin))
                .then(ReloadCommand.create(plugin))
                .then(ResetCommand.create(plugin))
                .then(RevealCommand.create(plugin))
                .then(SaveCommand.create(plugin))
                .then(UnregisterCommand.create(plugin))
                .build();
        return new BrigadierCommand(command);
    }
}
