package net.thenextlvl.commander.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.thenextlvl.commander.CommanderCommons;
import net.thenextlvl.commander.command.brigadier.SimpleCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class SaveCommand<S> extends SimpleCommand<S> {
    private SaveCommand(final CommanderCommons commons) {
        super(commons, "save", "commander.command.save");
    }

    public static <S> ArgumentBuilder<S, ?> create(final CommanderCommons commons) {
        final var command = new SaveCommand<S>(commons);
        return command.create().executes(command);
    }

    @Override
    public int run(final CommandContext<S> context) {
        final var sender = commons.brigadierAccess().audience(context.getSource());
        final var saved = commons.commandRegistry().save(true) & commons.permissionOverride().save(true);
        final var message = saved ? "command.saved" : "nothing.changed";
        commons.bundle().sendMessage(sender, message);
        return saved ? SINGLE_SUCCESS : 0;
    }
}
