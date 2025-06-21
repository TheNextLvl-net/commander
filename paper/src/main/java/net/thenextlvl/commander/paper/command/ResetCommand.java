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
    public static ArgumentBuilder<CommandSourceStack, ?> create(CommanderPlugin plugin) {
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
                        .executes(context -> reset(context, plugin)));
    }

    private static int reset(CommandContext<CommandSourceStack> context, CommanderPlugin plugin) {
        var sender = context.getSource().getSender();
        var command = context.getArgument("command", String.class);
        var reset = plugin.permissionOverride().reset(command)
                    | plugin.commandRegistry().register(command)
                    | plugin.commandRegistry().reveal(command);
        var message = reset ? "command.reset" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message, Placeholder.parsed("command", command));
        if (reset) {
            plugin.getServer().getOnlinePlayers().forEach(Player::updateCommands);
            plugin.conflictSave(sender);
        }
        return Command.SINGLE_SUCCESS;
    }
}
