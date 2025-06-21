package net.thenextlvl.commander.velocity.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import net.thenextlvl.commander.velocity.CommanderPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jspecify.annotations.NullMarked;

@NullMarked
class SaveCommand {
    public static ArgumentBuilder<CommandSource, ?> create(CommanderPlugin plugin) {
        return BrigadierCommand.literalArgumentBuilder("save")
                .executes(context -> save(context, plugin));
    }

    private static int save(CommandContext<CommandSource> context, CommanderPlugin plugin) {
        var sender = context.getSource();
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
