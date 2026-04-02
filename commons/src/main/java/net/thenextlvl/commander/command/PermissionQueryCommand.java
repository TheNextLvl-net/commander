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
final class PermissionQueryCommand<S> extends SimpleCommand<S> {
    private PermissionQueryCommand(final CommanderCommons commons) {
        super(commons, "query", "commander.command.permission.query");
    }

    public static <S> ArgumentBuilder<S, ?> create(final CommanderCommons commons) {
        final var command = new PermissionQueryCommand<S>(commons);
        return command.create().then(commons.<S>brigadierAccess().argument("command", StringArgumentType.string())
                .suggests(new CommandSuggestionProvider<>(commons))
                .executes(command));
    }

    @Override
    public int run(final CommandContext<S> context) {
        final var sender = commons.brigadierAccess().audience(context.getSource());
        final var command = context.getArgument("command", String.class);
        final var permission = commons.permissionOverride().permission(command);
        final var message = permission != null ? "permission.query.defined" : "permission.query.undefined";
        commons.bundle().sendMessage(sender, message,
                Placeholder.parsed("permission", String.valueOf(permission)),
                Placeholder.parsed("command", command));
        return SINGLE_SUCCESS;
    }
}
