package net.thenextlvl.commander.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.CommanderCommons;
import net.thenextlvl.commander.command.brigadier.SimpleCommand;
import net.thenextlvl.commander.command.suggestion.CommandSuggestionProvider;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class PermissionUnsetCommand<S> extends SimpleCommand<S> {
    private PermissionUnsetCommand(CommanderCommons commons) {
        super(commons, "unset", "commander.command.permission.unset");
    }

    public static <S> ArgumentBuilder<S, ?> create(CommanderCommons commons) {
        var command = new PermissionUnsetCommand<S>(commons);
        return command.create().then(commons.<S>brigadierAccess().argument("command", StringArgumentType.string())
                .suggests(new CommandSuggestionProvider<>(commons))
                .executes(command));
    }

    @Override
    public int run(CommandContext<S> context) throws CommandSyntaxException {
        var sender = commons.brigadierAccess().audience(context.getSource());
        var command = context.getArgument("command", String.class);
        var success = commons.permissionOverride().override(command, null);
        var message = success ? "permission.unset" : "nothing.changed";
        commons.bundle().sendMessage(sender, message,
                Placeholder.parsed("permission", "null"),
                Placeholder.parsed("command", command));
        if (success) commons.permissionConflictSave(sender);
        return success ? SINGLE_SUCCESS : 0;
    }
}
