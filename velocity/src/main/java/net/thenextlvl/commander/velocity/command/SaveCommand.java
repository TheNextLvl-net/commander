package net.thenextlvl.commander.velocity.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.commander.velocity.CommanderPlugin;

@RequiredArgsConstructor
class SaveCommand {
    private final CommanderPlugin plugin;

    public ArgumentBuilder<CommandSource, ?> create() {
        return BrigadierCommand.literalArgumentBuilder("save").executes(this::save);
    }

    private int save(CommandContext<CommandSource> context) {
        var sender = context.getSource();
        plugin.commandRegistry().getHiddenFile().save();
        plugin.commandRegistry().getUnregisteredFile().save();
        plugin.permissionOverride().getOverridesFile().save();
        plugin.bundle().sendMessage(sender, "command.saved");
        return Command.SINGLE_SUCCESS;
    }
}
