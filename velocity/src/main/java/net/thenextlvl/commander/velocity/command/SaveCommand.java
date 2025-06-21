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
    public static ArgumentBuilder<CommandSource, ?> create(CommanderPlugin plugin) {
        return BrigadierCommand.literalArgumentBuilder("save")
                .executes(context -> save(context, plugin));
    }

    private static int save(CommandContext<CommandSource> context, CommanderPlugin plugin) {
        var sender = context.getSource();
        var saved = plugin.commandRegistry().save(true) & plugin.permissionOverride().save(true);
        var message = saved ? "command.saved" : "command.save.conflict";
        plugin.bundle().sendMessage(sender, message);
        return Command.SINGLE_SUCCESS;
    }
}
