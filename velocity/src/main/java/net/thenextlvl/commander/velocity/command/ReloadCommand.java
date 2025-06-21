package net.thenextlvl.commander.velocity.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.velocity.CommanderPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
class ReloadCommand {
    public static ArgumentBuilder<CommandSource, ?> create(CommanderPlugin plugin) {
        return BrigadierCommand.literalArgumentBuilder("reload")
                .executes(context -> reload(context, plugin));
    }

    private static int reload(CommandContext<CommandSource> context, CommanderPlugin plugin) {
        var sender = context.getSource();
        try {
            var commands = plugin.commandRegistry().reload(sender);
            var permissions = plugin.permissionOverride().reload(sender);
            var success = commands || permissions;
            var message = success ? "command.reload.success" : "nothing.changed";
            plugin.bundle().sendMessage(sender, message);
            return success ? Command.SINGLE_SUCCESS : 0;
        } catch (Exception e) {
            plugin.bundle().sendMessage(sender, "command.reload.failed",
                    Placeholder.parsed("error", e.getMessage()));
            plugin.logger().warn("Failed to reload command configurations", e);
            return 0;
        }
    }
}
