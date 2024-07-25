package net.thenextlvl.commander.paper.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.paper.CommanderPlugin;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
class ResetCommand {
    private final CommanderPlugin plugin;

    public ArgumentBuilder<CommandSourceStack, ?> create() {
        return Commands.literal("reset")
                .then(Commands.argument("command", StringArgumentType.string())
                        .suggests((context, suggestions) -> {
                            plugin.commandRegistry().hiddenCommands().stream()
                                    .filter(s -> s.contains(suggestions.getRemaining()))
                                    .map(StringArgumentType::escapeIfRequired)
                                    .forEach(suggestions::suggest);
                            plugin.commandRegistry().unregisteredCommands().stream()
                                    .filter(s -> s.contains(suggestions.getRemaining()))
                                    .map(StringArgumentType::escapeIfRequired)
                                    .forEach(suggestions::suggest);
                            return suggestions.buildFuture();
                        })
                        .executes(this::reset));
    }

    private int reset(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var command = context.getArgument("command", String.class);
        var s1 = plugin.permissionOverride().reset(command);
        var s3 = plugin.commandRegistry().register(command);
        var s2 = plugin.commandRegistry().reveal(command);
        var message = s1 || s2 || s3 ? "command.reset" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message, Placeholder.parsed("command", command));
        return Command.SINGLE_SUCCESS;
    }
}
