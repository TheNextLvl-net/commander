package net.thenextlvl.commander.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.thenextlvl.commander.CommanderCommons;
import net.thenextlvl.commander.command.brigadier.SimpleCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class SaveCommand<S> extends SimpleCommand<S> {
    private SaveCommand(CommanderCommons commons) {
        super(commons, "save", "commander.command.save");
    }

    public static <S> ArgumentBuilder<S, ?> create(CommanderCommons commons) {
        var command = new SaveCommand<S>(commons);
        return command.create().executes(command);
    }

    @Override
    public int run(CommandContext<S> context) {
        var sender = commons.brigadierAccess().audience(context.getSource());
        var saved = commons.commandRegistry().save(true) & commons.permissionOverride().save(true);
        var message = saved ? "command.saved" : "nothing.changed";
        commons.bundle().sendMessage(sender, message);
        return saved ? SINGLE_SUCCESS : 0;
    }
}
