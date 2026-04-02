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
final class UnregisterCommand<S> extends SimpleCommand<S> {
    private UnregisterCommand(final CommanderCommons commons) {
        super(commons, "unregister", "commander.command.unregister");
    }

    public static <S> ArgumentBuilder<S, ?> create(final CommanderCommons commons) {
        final var command = new UnregisterCommand<S>(commons);
        return command.create().then(commons.<S>brigadierAccess().argument("command", StringArgumentType.string())
                .suggests(new CommandSuggestionProvider<>(commons))
                .executes(command));
    }

    @Override
    public int run(final CommandContext<S> context) {
        final var sender = commons.brigadierAccess().audience(context.getSource());
        final var command = context.getArgument("command", String.class);
        final var success = commons.commandRegistry().unregister(command);
        final var message = success ? "command.unregistered" : "nothing.changed";
        commons.bundle().sendMessage(sender, message, Placeholder.parsed("command", command));
        if (success) commons.unregisteredConflictSave(sender);
        return success ? SINGLE_SUCCESS : 0;
    }
}
