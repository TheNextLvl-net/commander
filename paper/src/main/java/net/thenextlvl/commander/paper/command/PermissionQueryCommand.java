package net.thenextlvl.commander.paper.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.paper.CommanderPlugin;
import net.thenextlvl.commander.paper.command.suggestion.CommandSuggestionProvider;
import org.jspecify.annotations.NullMarked;

@NullMarked
class PermissionQueryCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> create(CommanderPlugin plugin) {
        return Commands.literal("query")
                .then(Commands.argument("command", StringArgumentType.string())
                        .suggests(new CommandSuggestionProvider(plugin))
                        .executes(context -> query(context, plugin)));
    }

    private static int query(CommandContext<CommandSourceStack> context, CommanderPlugin plugin) {
        var sender = context.getSource().getSender();
        var command = context.getArgument("command", String.class);
        var registered = plugin.getServer().getCommandMap().getKnownCommands().get(command);
        var permission = registered != null ? registered.getPermission() : null;
        var message = registered == null ? "command.unknown" : permission != null ?
                "permission.query.defined" : "permission.query.undefined";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.parsed("permission", String.valueOf(permission)),
                Placeholder.parsed("command", command));
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }
}
