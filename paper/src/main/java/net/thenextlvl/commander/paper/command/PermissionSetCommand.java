package net.thenextlvl.commander.paper.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.paper.CommanderPlugin;
import net.thenextlvl.commander.paper.command.suggestion.CommandSuggestionProvider;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jspecify.annotations.NullMarked;

@NullMarked
class PermissionSetCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> create(CommanderPlugin plugin) {
        return Commands.literal("set")
                .then(Commands.argument("command", StringArgumentType.string())
                        .suggests(new CommandSuggestionProvider(plugin))
                        .then(Commands.argument("permission", StringArgumentType.string())
                                .suggests((context, suggestions) -> {
                                    plugin.getServer().getPluginManager().getPermissions().stream()
                                            .map(Permission::getName)
                                            .map(StringArgumentType::escapeIfRequired)
                                            .filter(s -> s.contains(suggestions.getRemaining()))
                                            .forEach(suggestions::suggest);
                                    return suggestions.buildFuture();
                                })
                                .executes(context -> set(context, plugin))));
    }

    private static int set(CommandContext<CommandSourceStack> context, CommanderPlugin plugin) {
        var sender = context.getSource().getSender();
        var command = context.getArgument("command", String.class);
        var permission = context.getArgument("permission", String.class);
        var success = plugin.permissionOverride().override(command, permission);
        var message = success ? "permission.set" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.parsed("permission", permission),
                Placeholder.parsed("command", command));
        if (success) {
            plugin.getServer().getOnlinePlayers().forEach(Player::updateCommands);
            plugin.autoSave(sender);
        }
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }
}
