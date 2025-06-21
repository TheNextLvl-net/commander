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
    public static ArgumentBuilder<CommandSourceStack, ?> create(CommanderPlugin plugin) {
        return Commands.literal("save")
                .executes(context -> save(context, plugin));
    }

    private static int save(CommandContext<CommandSourceStack> context, CommanderPlugin plugin) {
        var sender = context.getSource().getSender();
        var saved = plugin.commandRegistry().save(true) & plugin.permissionOverride().save(true);
        var message = saved ? "command.saved" : "command.save.conflict";
        plugin.bundle().sendMessage(sender, message);
        return Command.SINGLE_SUCCESS;
    }
}
