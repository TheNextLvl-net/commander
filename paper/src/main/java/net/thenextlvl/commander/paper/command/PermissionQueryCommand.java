package net.thenextlvl.commander.paper.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.paper.CommanderPlugin;
import org.bukkit.Bukkit;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
class PermissionQueryCommand {
    private final CommanderPlugin plugin;

    public ArgumentBuilder<CommandSourceStack, ?> create() {
        return Commands.literal("query")
                .then(Commands.argument("command", new CommandArgumentType(plugin))
                        .executes(this::query));
    }

    private int query(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var command = context.getArgument("command", String.class);
        var registered = Bukkit.getCommandMap().getKnownCommands().get(command);
        var permission = registered != null ? registered.getPermission() : null;
        var message = registered == null ? "command.unknown" : permission != null ?
                "permission.query.defined" : "permission.query.undefined";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.parsed("permission", String.valueOf(permission)),
                Placeholder.parsed("command", command));
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }
}
