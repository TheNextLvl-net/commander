package net.thenextlvl.commander.velocity.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import net.thenextlvl.commander.velocity.CommanderPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
class SaveCommand {
    private final CommanderPlugin plugin;

    SaveCommand(CommanderPlugin plugin) {
        this.plugin = plugin;
    }

    public ArgumentBuilder<CommandSource, ?> create() {
        return BrigadierCommand.literalArgumentBuilder("save").executes(this::save);
    }

    private int save(CommandContext<CommandSource> context) {
        var sender = context.getSource();
        plugin.commandRegistry().save();
        plugin.permissionOverride().save();
        plugin.bundle().sendMessage(sender, "command.saved");
        return Command.SINGLE_SUCCESS;
    }
}
