package net.thenextlvl.commander.velocity.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.velocity.CommanderPlugin;

@RequiredArgsConstructor
class UnregisterCommand {
    private final CommanderPlugin plugin;

    public ArgumentBuilder<CommandSource, ?> create() {
        return BrigadierCommand.literalArgumentBuilder("unregister")
                .then(BrigadierCommand.requiredArgumentBuilder("command", StringArgumentType.string())
                        .suggests(new CommandSuggestionProvider(plugin))
                        .executes(this::unregister));
    }

    private int unregister(CommandContext<CommandSource> context) {
        var sender = context.getSource();
        var command = context.getArgument("command", String.class);
        var success = plugin.commandRegistry().unregister(command);
        var message = success ? "command.unregistered" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message, Placeholder.parsed("command", command));
        return Command.SINGLE_SUCCESS;
    }
}
