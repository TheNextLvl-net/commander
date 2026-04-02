package net.thenextlvl.commander.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.CommanderCommons;
import net.thenextlvl.commander.command.brigadier.SimpleCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class ReloadCommand<S> extends SimpleCommand<S> {
    private ReloadCommand(final CommanderCommons commons) {
        super(commons, "reload", "commander.command.reload");
    }

    public static <S> ArgumentBuilder<S, ?> create(final CommanderCommons plugin) {
        final var command = new ReloadCommand<S>(plugin);
        return command.create().executes(command);
    }

    @Override
    public int run(final CommandContext<S> context) {
        final var sender = commons.brigadierAccess().audience(context.getSource());
        try {
            final var success = commons.commandRegistry().reload(sender) | commons.permissionOverride().reload(sender);
            commons.bundle().sendMessage(sender, success ? "command.reload.success" : "nothing.changed");
            return success ? SINGLE_SUCCESS : 0;
        } catch (final Exception e) {
            commons.bundle().sendMessage(sender, "command.reload.failed",
                    Placeholder.parsed("error", e.getMessage()));
            commons.logger().warn("Failed to reload command configurations", e);
            return 0;
        }
    }
}
