package net.thenextlvl.commander.command.brigadier;

import com.mojang.brigadier.Command;
import net.thenextlvl.commander.CommanderCommons;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public abstract class SimpleCommand<S> extends BrigadierCommand<S> implements Command<S> {
    protected SimpleCommand(final CommanderCommons commons, final String name, @Nullable final String permission) {
        super(commons, name, permission);
    }
}
