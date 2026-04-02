package net.thenextlvl.commander.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.CommanderCommons;
import net.thenextlvl.commander.command.brigadier.SimpleCommand;
import net.thenextlvl.commander.command.suggestion.CommandSuggestionProvider;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class PermissionSetCommand<S> extends SimpleCommand<S> {
    private PermissionSetCommand(final CommanderCommons commons) {
        super(commons, "set", "commander.command.permission.set");
    }

    public static <S> ArgumentBuilder<S, ?> create(final CommanderCommons commons) {
        final var command = new PermissionSetCommand<S>(commons);
        return command.create().then(commons.<S>brigadierAccess().argument("command", StringArgumentType.string())
                .suggests(new CommandSuggestionProvider<>(commons))
                .then(commons.<S>brigadierAccess().argument("permission", StringArgumentType.string())
                        .suggests((context, suggestions) -> {
                            commons.getKnownPermissions()
                                    .map(StringArgumentType::escapeIfRequired)
                                    .filter(s -> s.contains(suggestions.getRemaining()))
                                    .forEach(suggestions::suggest);
                            return suggestions.buildFuture();
                        }).executes(command)));
    }

    @Override
    public int run(final CommandContext<S> context) {
        final var sender = commons.brigadierAccess().audience(context.getSource());
        final var command = context.getArgument("command", String.class);
        final var permission = context.getArgument("permission", String.class);
        final var success = commons.permissionOverride().override(command, permission);
        final var message = success ? "permission.set" : "nothing.changed";
        commons.bundle().sendMessage(sender, message,
                Placeholder.parsed("permission", permission),
                Placeholder.parsed("command", command));
        if (success) commons.permissionConflictSave(sender);
        return success ? SINGLE_SUCCESS : 0;
    }
}
