package net.thenextlvl.commander.paper.command;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.thenextlvl.commander.paper.CommanderPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CommanderCommand {
    public static void register(CommanderPlugin plugin) {
        var command = Commands.literal(CommanderPlugin.ROOT_COMMAND)
                .requires(stack -> stack.getSender().hasPermission("commander.admin"))
                .then(HideCommand.create(plugin))
                .then(PermissionCommand.create(plugin))
                .then(RegisterCommand.create(plugin))
                .then(ReloadCommand.create(plugin))
                .then(ResetCommand.create(plugin))
                .then(RevealCommand.create(plugin))
                .then(SaveCommand.create(plugin))
                .then(UnregisterCommand.create(plugin))
                .build();
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(event ->
                event.registrar().register(command)));
    }
}
