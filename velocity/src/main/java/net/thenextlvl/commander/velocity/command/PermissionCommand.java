package net.thenextlvl.commander.velocity.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import net.thenextlvl.commander.velocity.CommanderPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
class PermissionCommand {
    public static ArgumentBuilder<CommandSource, ?> create(CommanderPlugin plugin) {
        return BrigadierCommand.literalArgumentBuilder("permission")
                .then(PermissionQueryCommand.create(plugin))
                .then(PermissionResetCommand.create(plugin))
                .then(PermissionSetCommand.create(plugin));
    }
}
