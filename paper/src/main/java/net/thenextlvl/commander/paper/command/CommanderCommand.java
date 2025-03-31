package net.thenextlvl.commander.paper.command;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.thenextlvl.commander.paper.CommanderPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CommanderCommand {

    public void register(CommanderPlugin plugin) {
        var command = Commands.literal("command")
                .requires(stack -> stack.getSender().hasPermission("commander.admin"))
                .then(new HideCommand(plugin).create())
                .then(new PermissionCommand(plugin).create())
                .then(new RegisterCommand(plugin).create())
                .then(new ReloadCommand(plugin).create())
                .then(new ResetCommand(plugin).create())
                .then(new RevealCommand(plugin).create())
                .then(new SaveCommand(plugin).create())
                .then(new UnregisterCommand(plugin).create())
                .build();
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(event ->
                event.registrar().register(command)));
    }
}
