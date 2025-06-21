package net.thenextlvl.commander.paper.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.commander.paper.CommanderPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jspecify.annotations.NullMarked;

@NullMarked
class SaveCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> create(CommanderPlugin plugin) {
        return Commands.literal("save")
                .executes(context -> save(context, plugin));
    }

    private static int save(CommandContext<CommandSourceStack> context, CommanderPlugin plugin) {
        var sender = context.getSource().getSender();
        var reg = plugin.commandRegistry().save(true);
        var perm = plugin.permissionOverride().save(true);
        var message = reg && perm ? "command.saved" : "command.save.conflict";
        var mm = MiniMessage.miniMessage();
        var serialized = mm.serialize(plugin.bundle().component(message, sender));
        serialized = serialized.replace("{ROOTCMD}", CommanderPlugin.ROOT_COMMAND);
        sender.sendMessage(mm.deserialize(serialized));
        return Command.SINGLE_SUCCESS;
    }
}
