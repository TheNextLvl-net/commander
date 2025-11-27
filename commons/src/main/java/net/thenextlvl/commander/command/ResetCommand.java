package net.thenextlvl.commander.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.CommanderCommons;
import net.thenextlvl.commander.command.brigadier.SimpleCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class ResetCommand<S> extends SimpleCommand<S> {
    private ResetCommand(CommanderCommons commons) {
        super(commons, "reset", "commander.command.reset");
    }

    public static <S> ArgumentBuilder<S, ?> create(CommanderCommons commons) {
        var command = new ResetCommand<S>(commons);
        return command.create().then(commons.<S>brigadierAccess().argument("command", StringArgumentType.string())
                .suggests((context, suggestions) -> {
                    commons.commandRegistry().hiddenCommands().stream()
                            .map(StringArgumentType::escapeIfRequired)
                            .filter(s -> s.contains(suggestions.getRemaining()))
                            .forEach(suggestions::suggest);
                    commons.commandRegistry().unregisteredCommands().stream()
                            .map(StringArgumentType::escapeIfRequired)
                            .filter(s -> s.contains(suggestions.getRemaining()))
                            .forEach(suggestions::suggest);
                    commons.permissionOverride().overrides().keySet().stream()
                            .map(StringArgumentType::escapeIfRequired)
                            .filter(s -> s.contains(suggestions.getRemaining()))
                            .forEach(suggestions::suggest);
                    return suggestions.buildFuture();
                }).executes(command));
    }

    @Override
    public int run(CommandContext<S> context) {
        var sender = commons.brigadierAccess().audience(context.getSource());
        var command = context.getArgument("command", String.class);
        var reset = commons.permissionOverride().reset(command)
                | commons.commandRegistry().register(command)
                | commons.commandRegistry().reveal(command);
        var message = reset ? "command.reset" : "nothing.changed";
        commons.bundle().sendMessage(sender, message, Placeholder.parsed("command", command));
        if (reset) commons.conflictSave(sender);
        return reset ? SINGLE_SUCCESS : 0;
    }
}
