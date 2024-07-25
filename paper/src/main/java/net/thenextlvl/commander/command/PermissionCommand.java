package net.thenextlvl.commander.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.commander.CommanderPlugin;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
class PermissionCommand {
    private final CommanderPlugin plugin;

    public ArgumentBuilder<CommandSourceStack, ?> create() {
        return Commands.literal("permission")
                .then(new PermissionQueryCommand(plugin).create())
                .then(new PermissionResetCommand(plugin).create())
                .then(new PermissionSetCommand(plugin).create())
                .then(new PermissionUnsetCommand(plugin).create());
    }
}
