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
class PermissionResetCommand {
    public static ArgumentBuilder<CommandSource, ?> create(CommanderPlugin plugin) {
        return BrigadierCommand.literalArgumentBuilder("reset")
                .then(BrigadierCommand.requiredArgumentBuilder("command", StringArgumentType.string())
                        .suggests((context, suggestions) -> {
                            plugin.permissionOverride().overrides().keySet().stream()
                                    .filter(s -> !plugin.commandRegistry().isUnregistered(s))
                                    .map(StringArgumentType::escapeIfRequired)
                                    .filter(s -> s.contains(suggestions.getRemaining()))
                                    .forEach(suggestions::suggest);
                            return suggestions.buildFuture();
                        })
                        .executes(context -> reset(context, plugin)));
    }

    private static int reset(CommandContext<CommandSource> context, CommanderPlugin plugin) {
        var sender = context.getSource();
        var command = context.getArgument("command", String.class);
        var success = plugin.permissionOverride().reset(command);
        var message = success ? "permission.reset" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message, Placeholder.parsed("command", command),
                Placeholder.parsed("permission", "null"));
        if (success) plugin.permissionConflictSave(sender);
        return Command.SINGLE_SUCCESS;
    }
}
