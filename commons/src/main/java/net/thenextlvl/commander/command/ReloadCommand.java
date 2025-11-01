package net.thenextlvl.commander.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.CommanderCommons;
import net.thenextlvl.commander.command.brigadier.SimpleCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class ReloadCommand<S> extends SimpleCommand<S> {
    private ReloadCommand(CommanderCommons commons) {
        super(commons, "reload", "commander.command.reload");
    }

    public static <S> ArgumentBuilder<S, ?> create(CommanderCommons plugin) {
        var command = new ReloadCommand<S>(plugin);
        return command.create().executes(command);
    }

    @Override
    public int run(CommandContext<S> context) throws CommandSyntaxException {
        var sender = commons.brigadierAccess().audience(context.getSource());
        try {
            var success = commons.commandRegistry().reload(sender) | commons.permissionOverride().reload(sender);
            commons.bundle().sendMessage(sender, success ? "command.reload.success" : "nothing.changed");
            return success ? SINGLE_SUCCESS : 0;
        } catch (Exception e) {
            commons.bundle().sendMessage(sender, "command.reload.failed",
                    Placeholder.parsed("error", e.getMessage()));
            commons.logger().warn("Failed to reload command configurations", e);
            return 0;
        }
    }
}
