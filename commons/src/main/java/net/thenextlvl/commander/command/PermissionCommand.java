package net.thenextlvl.commander.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.thenextlvl.commander.CommanderCommons;
import net.thenextlvl.commander.command.brigadier.BrigadierCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class PermissionCommand<S> extends BrigadierCommand<S> {
    private PermissionCommand(CommanderCommons commons) {
        super(commons, "permission", "commander.command.permission");
    }

    public static <S> ArgumentBuilder<S, ?> create(CommanderCommons commons) {
        var command = new PermissionCommand<S>(commons);
        return command.create()
                .then(PermissionQueryCommand.<S>create(commons))
                .then(PermissionResetCommand.<S>create(commons))
                .then(PermissionSetCommand.<S>create(commons));
    }
}
