package net.thenextlvl.commander.paper.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.paper.CommanderPlugin;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
class ReloadCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> create(CommanderPlugin plugin) {
        return Commands.literal("reload").executes(context -> reload(context, plugin));
    }

    private static int reload(CommandContext<CommandSourceStack> context, CommanderPlugin plugin) {
        var sender = context.getSource().getSender();
        try {
            var commands = plugin.commandRegistry().reload(sender);
            var permissions = plugin.permissionOverride().reload(sender);
            var success = commands || permissions;
            if (success && sender instanceof Player player) player.updateCommands();
            var message = success ? "command.reload.success" : "nothing.changed";
            plugin.bundle().sendMessage(sender, message);
            return success ? Command.SINGLE_SUCCESS : 0;
        } catch (Exception e) {
            plugin.bundle().sendMessage(sender, "command.reload.failed",
                    Placeholder.parsed("error", e.getMessage()));
            return 0;
        }
    }
}
