package net.thenextlvl.commander.velocity.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.velocity.CommanderPlugin;

@RequiredArgsConstructor
class ReloadCommand {
    private final CommanderPlugin plugin;

    public ArgumentBuilder<CommandSource, ?> create() {
        return BrigadierCommand.literalArgumentBuilder("reload").executes(this::reload);
    }

    private int reload(CommandContext<CommandSource> context) {
        var sender = context.getSource();
        try {
            var commands = plugin.commandRegistry().reload(sender);
            var permissions = plugin.permissionOverride().reload(sender);
            var success = commands || permissions;
            var message = success ? "command.reload.success" : "nothing.changed";
            plugin.bundle().sendMessage(sender, message);
            return success ? Command.SINGLE_SUCCESS : 0;
        } catch (Exception e) {
            plugin.bundle().sendMessage(sender, "command.reload.failed",
                    Placeholder.parsed("error", e.getMessage()));
            return 0;
        }
    }
}
