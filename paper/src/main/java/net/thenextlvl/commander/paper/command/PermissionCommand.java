package net.thenextlvl.commander.paper.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.commander.paper.CommanderPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
class PermissionCommand {
    private final CommanderPlugin plugin;

    PermissionCommand(CommanderPlugin plugin) {
        this.plugin = plugin;
    }

    public ArgumentBuilder<CommandSourceStack, ?> create() {
        return Commands.literal("permission")
                .then(new PermissionQueryCommand(plugin).create())
                .then(new PermissionResetCommand(plugin).create())
                .then(new PermissionSetCommand(plugin).create())
                .then(new PermissionUnsetCommand(plugin).create());
    }
}
