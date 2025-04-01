package net.thenextlvl.commander.paper.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.paper.CommanderPlugin;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
class UnregisterCommand {
    private final CommanderPlugin plugin;

    UnregisterCommand(CommanderPlugin plugin) {
        this.plugin = plugin;
    }

    public ArgumentBuilder<CommandSourceStack, ?> create() {
        return Commands.literal("unregister")
                .then(Commands.argument("command", StringArgumentType.string())
                        .suggests(new CommandSuggestionProvider(plugin))
                        .executes(this::unregister));
    }

    private int unregister(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var command = context.getArgument("command", String.class);
        var success = plugin.commandRegistry().unregister(command);
        var message = success ? "command.unregistered" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message, Placeholder.parsed("command", command));
        if (success) plugin.getServer().getOnlinePlayers().forEach(Player::updateCommands);
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }
}
