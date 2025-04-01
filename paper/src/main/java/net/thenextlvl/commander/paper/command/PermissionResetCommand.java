package net.thenextlvl.commander.paper.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.paper.CommanderPlugin;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

@NullMarked
class PermissionResetCommand {
    private final CommanderPlugin plugin;

    PermissionResetCommand(CommanderPlugin plugin) {
        this.plugin = plugin;
    }

    public ArgumentBuilder<CommandSourceStack, ?> create() {
        return Commands.literal("reset")
                .then(Commands.argument("command", StringArgumentType.string())
                        .suggests((context, suggestions) -> {
                            plugin.permissionOverride().originalPermissions().keySet().stream()
                                    .filter(s -> !plugin.commandRegistry().isUnregistered(s))
                                    .map(StringArgumentType::escapeIfRequired)
                                    .filter(s -> s.contains(suggestions.getRemaining()))
                                    .forEach(suggestions::suggest);
                            return suggestions.buildFuture();
                        })
                        .executes(this::reset));
    }

    private int reset(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var command = context.getArgument("command", String.class);
        var success = plugin.permissionOverride().reset(command);
        var message = success ? "permission.reset" : "nothing.changed";
        var permission = Optional.ofNullable(plugin.getServer().getCommandMap().getCommand(command))
                .map(org.bukkit.command.Command::getPermission)
                .orElse("null");
        plugin.bundle().sendMessage(sender, message, Placeholder.parsed("command", command),
                Placeholder.parsed("permission", permission));
        if (success) plugin.getServer().getOnlinePlayers().forEach(Player::updateCommands);
        return Command.SINGLE_SUCCESS;
    }
}
