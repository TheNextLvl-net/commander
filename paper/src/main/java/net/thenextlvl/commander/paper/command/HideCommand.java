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
class HideCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> create(CommanderPlugin plugin) {
        return Commands.literal("hide")
                .then(Commands.argument("command", StringArgumentType.string())
                        .suggests((context, suggestions) -> {
                            plugin.getServer().getCommandMap().getKnownCommands().values().stream()
                                    .map(org.bukkit.command.Command::getLabel)
                                    .filter(s -> !plugin.commandRegistry().isUnregistered(s))
                                    .filter(s -> !plugin.commandRegistry().isHidden(s))
                                    .map(StringArgumentType::escapeIfRequired)
                                    .filter(s -> s.contains(suggestions.getRemaining()))
                                    .forEach(suggestions::suggest);
                            return suggestions.buildFuture();
                        })
                        .executes(context -> hide(context, plugin)));
    }

    private static int hide(CommandContext<CommandSourceStack> context, CommanderPlugin plugin) {
        var sender = context.getSource().getSender();
        var command = context.getArgument("command", String.class);
        var success = plugin.commandRegistry().hide(command);
        var message = success ? "command.hidden" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message, Placeholder.parsed("command", command));
        if (success) {
            plugin.getServer().getOnlinePlayers().forEach(Player::updateCommands);
            plugin.hiddenConflictSave(sender);
        }
        return Command.SINGLE_SUCCESS;
    }
}
