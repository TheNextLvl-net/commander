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

import java.util.Objects;

@NullMarked
class PermissionSetCommand {
    private final CommanderPlugin plugin;

    PermissionSetCommand(CommanderPlugin plugin) {
        this.plugin = plugin;
    }

    public ArgumentBuilder<CommandSource, ?> create() {
        return BrigadierCommand.literalArgumentBuilder("set")
                .then(BrigadierCommand.requiredArgumentBuilder("command", StringArgumentType.string())
                        .suggests(new CommandSuggestionProvider(plugin))
                        .then(BrigadierCommand.requiredArgumentBuilder("permission", StringArgumentType.string())
                                .suggests((context, suggestions) -> {
                                    plugin.permissionOverride().overrides().values().stream()
                                            .filter(Objects::nonNull)
                                            .map(StringArgumentType::escapeIfRequired)
                                            .filter(s -> s.contains(suggestions.getRemaining()))
                                            .forEach(suggestions::suggest);
                                    return suggestions.buildFuture();
                                })
                                .executes(this::set)));
    }

    private int set(CommandContext<CommandSource> context) {
        var sender = context.getSource();
        var command = context.getArgument("command", String.class);
        var permission = context.getArgument("permission", String.class);
        var success = plugin.permissionOverride().override(command, permission);
        var message = success ? "permission.set" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.parsed("permission", permission),
                Placeholder.parsed("command", command));
        return Command.SINGLE_SUCCESS;
    }
}
