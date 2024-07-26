package net.thenextlvl.commander.paper.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.paper.CommanderPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
class PermissionUnsetCommand {
    private final CommanderPlugin plugin;

    public ArgumentBuilder<CommandSourceStack, ?> create() {
        return Commands.literal("unset")
                .then(Commands.argument("command", StringArgumentType.string())
                        .suggests((context, suggestions) -> {
                            Bukkit.getCommandMap().getKnownCommands().values().stream()
                                    .map(Command::getLabel)
                                    .filter(s -> !plugin.commandRegistry().isUnregistered(s))
                                    .map(StringArgumentType::escapeIfRequired)
                                    .filter(s -> s.contains(suggestions.getRemaining()))
                                    .forEach(suggestions::suggest);
                            return suggestions.buildFuture();
                        })
                        .executes(this::unset));
    }

    private int unset(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var command = context.getArgument("command", String.class);
        var success = plugin.permissionOverride().override(command, null);
        var message = success ? "permission.set" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.parsed("permission", "null"),
                Placeholder.parsed("command", command));
        if (success) Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }
}
