package net.thenextlvl.commander.velocity.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.velocity.CommanderPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
class HideCommand {
    public static ArgumentBuilder<CommandSource, ?> create(CommanderPlugin plugin) {
        return BrigadierCommand.literalArgumentBuilder("hide")
                .then(BrigadierCommand.requiredArgumentBuilder("command", StringArgumentType.string())
                        .suggests((context, suggestions) -> {
                            plugin.server().getCommandManager().getAliases().stream()
                                    .filter(s -> !plugin.commandRegistry().isUnregistered(s))
                                    .filter(s -> !plugin.commandRegistry().isHidden(s))
                                    .map(StringArgumentType::escapeIfRequired)
                                    .filter(s -> s.contains(suggestions.getRemaining()))
                                    .forEach(suggestions::suggest);
                            return suggestions.buildFuture();
                        })
                        .executes(context -> hide(context, plugin)));
    }

    private static int hide(CommandContext<CommandSource> context, CommanderPlugin plugin) {
        var sender = context.getSource();
        var command = context.getArgument("command", String.class);
        var success = plugin.commandRegistry().hide(command);
        var message = success ? "command.hidden" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message, Placeholder.parsed("command", command));
        if (success) plugin.hiddenConflictSave(sender);
        return Command.SINGLE_SUCCESS;
    }
}
