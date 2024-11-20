package net.thenextlvl.commander.velocity.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.commander.velocity.CommanderPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
@RequiredArgsConstructor
class PermissionCommand {
    private final CommanderPlugin plugin;

    public ArgumentBuilder<CommandSource, ?> create() {
        return BrigadierCommand.literalArgumentBuilder("permission")
                .then(new PermissionQueryCommand(plugin).create())
                .then(new PermissionResetCommand(plugin).create())
                .then(new PermissionSetCommand(plugin).create());
    }
}
