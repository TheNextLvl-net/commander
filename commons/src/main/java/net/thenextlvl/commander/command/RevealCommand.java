package net.thenextlvl.commander.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.CommanderCommons;
import net.thenextlvl.commander.command.brigadier.SimpleCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class RevealCommand<S> extends SimpleCommand<S> {
    private RevealCommand(CommanderCommons commons) {
        super(commons, "reveal", "commander.command.reveal");
    }

    public static <S> ArgumentBuilder<S, ?> create(CommanderCommons commons) {
        var command = new RevealCommand<S>(commons);
        return command.create()
                .then(commons.<S>brigadierAccess().argument("command", StringArgumentType.string())
                        .suggests((context, suggestions) -> {
                            commons.commandRegistry().hiddenCommands().stream()
                                    .filter(s -> !commons.commandRegistry().isUnregistered(s))
                                    .map(StringArgumentType::escapeIfRequired)
                                    .filter(s -> s.contains(suggestions.getRemaining()))
                                    .forEach(suggestions::suggest);
                            return suggestions.buildFuture();
                        })
                        .executes(command));
    }

    @Override
    public int run(CommandContext<S> context) {
        var sender = commons.brigadierAccess().audience(context.getSource());
        var command = context.getArgument("command", String.class);
        var success = commons.commandRegistry().reveal(command);
        var message = success ? "command.revealed" : "nothing.changed";
        commons.bundle().sendMessage(sender, message, Placeholder.parsed("command", command));
        if (success) commons.hiddenConflictSave(sender);
        return success ? SINGLE_SUCCESS : 0;
    }
}
