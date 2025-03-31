package net.thenextlvl.commander.paper.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.paper.CommanderPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
@RequiredArgsConstructor
class PermissionQueryCommand {
    private final CommanderPlugin plugin;

    public ArgumentBuilder<CommandSourceStack, ?> create() {
        return Commands.literal("query")
                .then(Commands.argument("command", StringArgumentType.string())
                        .suggests(new CommandSuggestionProvider(plugin))
                        .executes(this::query));
    }

    private int query(CommandContext<CommandSourceStack> context) {
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
