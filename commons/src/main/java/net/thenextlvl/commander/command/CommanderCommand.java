package net.thenextlvl.commander.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import net.thenextlvl.commander.CommanderCommons;
import net.thenextlvl.commander.command.brigadier.BrigadierCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class CommanderCommand<S> extends BrigadierCommand<S> {
    private CommanderCommand(CommanderCommons commons) {
        super(commons, commons.getRootCommand(), "commander.command");
    }

    public static <S> LiteralCommandNode<S> create(CommanderCommons commons) {
        return new CommanderCommand<S>(commons).create()
                .then(HideCommand.<S>create(commons))
                .then(PermissionCommand.<S>create(commons))
                .then(RegisterCommand.<S>create(commons))
                .then(ReloadCommand.<S>create(commons))
                .then(ResetCommand.<S>create(commons))
                .then(RevealCommand.<S>create(commons))
                .then(SaveCommand.<S>create(commons))
                .then(UnregisterCommand.<S>create(commons))
                .build();
    }
}
