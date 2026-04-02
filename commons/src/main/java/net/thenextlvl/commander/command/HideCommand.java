package net.thenextlvl.commander.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.CommanderCommons;
import net.thenextlvl.commander.command.brigadier.SimpleCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class HideCommand<S> extends SimpleCommand<S> {
    private HideCommand(final CommanderCommons commons) {
        super(commons, "hide", "commander.command.hide");
    }

    public static <S> ArgumentBuilder<S, ?> create(final CommanderCommons commons) {
        final var command = new HideCommand<S>(commons);
        return command.create().then(commons.<S>brigadierAccess().argument("command", StringArgumentType.string())
                .suggests((context, suggestions) -> {
                    commons.getKnownCommands()
                            .filter(s -> !commons.commandRegistry().isUnregistered(s))
                            .filter(s -> !commons.commandRegistry().isHidden(s))
                            .map(StringArgumentType::escapeIfRequired)
                            .filter(s -> s.contains(suggestions.getRemaining()))
                            .forEach(suggestions::suggest);
                    return suggestions.buildFuture();
                }).executes(command));
    }

    @Override
    public int run(final CommandContext<S> context) {
        final var sender = commons.brigadierAccess().audience(context.getSource());
        final var command = context.getArgument("command", String.class);
        final var success = commons.commandRegistry().hide(command);
        final var message = success ? "command.hidden" : "nothing.changed";
        commons.bundle().sendMessage(sender, message, Placeholder.parsed("command", command));
        if (success) commons.hiddenConflictSave(sender);
        return success ? SINGLE_SUCCESS : 0;
    }
}
