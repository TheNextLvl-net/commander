package net.thenextlvl.commander.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.CommanderCommons;
import net.thenextlvl.commander.command.brigadier.SimpleCommand;
import net.thenextlvl.commander.command.suggestion.PermissionResetSuggestionProvider;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class PermissionResetCommand<S> extends SimpleCommand<S> {
    private PermissionResetCommand(final CommanderCommons commons) {
        super(commons, "reset", "commander.command.permission.reset");
    }

    public static <S> ArgumentBuilder<S, ?> create(final CommanderCommons plugin) {
        final var command = new PermissionResetCommand<S>(plugin);
        return command.create().then(plugin.<S>brigadierAccess().argument("command", StringArgumentType.string())
                .suggests(new PermissionResetSuggestionProvider<>(plugin))
                .executes(command));
    }

    @Override
    public int run(final CommandContext<S> context) {
        final var sender = commons.brigadierAccess().audience(context.getSource());
        final var command = context.getArgument("command", String.class);
        final var success = commons.permissionOverride().reset(command);
        final var message = success ? "permission.reset" : "nothing.changed";
        commons.bundle().sendMessage(sender, message, Placeholder.parsed("command", command));
        if (success) commons.permissionConflictSave(sender);
        return success ? SINGLE_SUCCESS : 0;
    }
}
