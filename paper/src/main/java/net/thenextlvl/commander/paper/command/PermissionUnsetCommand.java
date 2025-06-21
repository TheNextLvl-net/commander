package net.thenextlvl.commander.paper.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.paper.CommanderPlugin;
import net.thenextlvl.commander.paper.command.suggestion.CommandSuggestionProvider;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
class PermissionUnsetCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> create(CommanderPlugin plugin) {
        return Commands.literal("unset")
                .then(Commands.argument("command", StringArgumentType.string())
                        .suggests(new CommandSuggestionProvider(plugin))
                        .executes(context -> unset(context, plugin)));
    }

    private static int unset(CommandContext<CommandSourceStack> context, CommanderPlugin plugin) {
        var sender = context.getSource().getSender();
        var command = context.getArgument("command", String.class);
        var success = plugin.permissionOverride().override(command, null);
        var message = success ? "permission.set" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.parsed("permission", "null"),
                Placeholder.parsed("command", command));
        if (success) {
            plugin.getServer().getOnlinePlayers().forEach(Player::updateCommands);
            plugin.permissionConflictSave(sender);
        }
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }
}
