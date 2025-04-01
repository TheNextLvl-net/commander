package net.thenextlvl.commander.paper.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.commander.paper.CommanderPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
class PermissionCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> create(CommanderPlugin plugin) {
        return Commands.literal("permission")
                .then(PermissionQueryCommand.create(plugin))
                .then(PermissionResetCommand.create(plugin))
                .then(PermissionSetCommand.create(plugin))
                .then(PermissionUnsetCommand.create(plugin));
    }
}
