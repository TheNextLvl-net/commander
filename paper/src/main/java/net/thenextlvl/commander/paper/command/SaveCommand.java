package net.thenextlvl.commander.paper.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.commander.paper.CommanderPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
class SaveCommand {
    private final CommanderPlugin plugin;

    SaveCommand(CommanderPlugin plugin) {
        this.plugin = plugin;
    }

    public ArgumentBuilder<CommandSourceStack, ?> create() {
        return Commands.literal("save").executes(this::save);
    }

    private int save(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        plugin.commandRegistry().save();
        plugin.permissionOverride().save();
        plugin.bundle().sendMessage(sender, "command.saved");
        return Command.SINGLE_SUCCESS;
    }
}
