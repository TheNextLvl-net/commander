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

@NullMarked
class ResetCommand {
    private final CommanderPlugin plugin;

    ResetCommand(CommanderPlugin plugin) {
        this.plugin = plugin;
    }

    public ArgumentBuilder<CommandSourceStack, ?> create() {
        return Commands.literal("reset")
                .then(Commands.argument("command", StringArgumentType.string())
                        .suggests((context, suggestions) -> {
                            plugin.commandRegistry().hiddenCommands().stream()
                                    .map(StringArgumentType::escapeIfRequired)
                                    .filter(s -> s.contains(suggestions.getRemaining()))
                                    .forEach(suggestions::suggest);
                            plugin.commandRegistry().unregisteredCommands().stream()
                                    .map(StringArgumentType::escapeIfRequired)
                                    .filter(s -> s.contains(suggestions.getRemaining()))
                                    .forEach(suggestions::suggest);
                            plugin.permissionOverride().originalPermissions().keySet().stream()
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
        var s1 = plugin.permissionOverride().reset(command);
        var s2 = plugin.commandRegistry().register(command);
        var s3 = plugin.commandRegistry().reveal(command);
        var message = s1 || s2 || s3 ? "command.reset" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message, Placeholder.parsed("command", command));
        if (s1 || s2 || s3) plugin.getServer().getOnlinePlayers().forEach(Player::updateCommands);
        return Command.SINGLE_SUCCESS;
    }
}
