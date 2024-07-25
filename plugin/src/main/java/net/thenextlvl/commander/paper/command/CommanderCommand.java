package net.thenextlvl.commander.paper.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.paper.CommanderPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
public class CommanderCommand {
    private final CommanderPlugin plugin;

    public void register() {
        var command = Commands.literal("command")
                .requires(stack -> stack.getSender().hasPermission("commander.admin"))
                .then(Commands.literal("hide")
                        .then(Commands.argument("command", StringArgumentType.string())
                                .suggests((context, suggestions) -> {
                                    Bukkit.getCommandMap().getKnownCommands().values().stream()
                                            .map(org.bukkit.command.Command::getLabel)
                                            .filter(s -> !plugin.commandRegistry().isHidden(s))
                                            .filter(s -> s.contains(suggestions.getRemaining()))
                                            .map(StringArgumentType::escapeIfRequired)
                                            .forEach(suggestions::suggest);
                                    return suggestions.buildFuture();
                                })
                                .executes(this::hide)))
                .then(Commands.literal("permission")
                        .then(Commands.literal("query")
                                .then(Commands.argument("command", StringArgumentType.string())
                                        .suggests((context, suggestions) -> {
                                            Bukkit.getCommandMap().getKnownCommands().values().stream()
                                                    .map(org.bukkit.command.Command::getLabel)
                                                    .filter(s -> s.contains(suggestions.getRemaining()))
                                                    .map(StringArgumentType::escapeIfRequired)
                                                    .forEach(suggestions::suggest);
                                            return suggestions.buildFuture();
                                        })
                                        .executes(this::permissionQuery)))
                        .then(Commands.literal("reset")
                                .then(Commands.argument("command", StringArgumentType.string())
                                        .suggests((context, suggestions) -> {
                                            plugin.permissionOverride().originalPermissions().keySet().stream()
                                                    .filter(s -> s.contains(suggestions.getRemaining()))
                                                    .map(StringArgumentType::escapeIfRequired)
                                                    .forEach(suggestions::suggest);
                                            return suggestions.buildFuture();
                                        })
                                        .executes(this::permissionReset)))
                        .then(Commands.literal("set")
                                .then(Commands.argument("command", StringArgumentType.string())
                                        .suggests((context, suggestions) -> {
                                            Bukkit.getCommandMap().getKnownCommands().values().stream()
                                                    .map(org.bukkit.command.Command::getLabel)
                                                    .filter(s -> s.contains(suggestions.getRemaining()))
                                                    .map(StringArgumentType::escapeIfRequired)
                                                    .forEach(suggestions::suggest);
                                            return suggestions.buildFuture();
                                        })
                                        .then(Commands.argument("permission", StringArgumentType.string())
                                                .suggests((context, suggestions) -> {
                                                    Bukkit.getPluginManager().getPermissions().stream()
                                                            .map(Permission::getName)
                                                            .filter(s -> s.contains(suggestions.getRemaining()))
                                                            .map(StringArgumentType::escapeIfRequired)
                                                            .forEach(suggestions::suggest);
                                                    return suggestions.buildFuture();
                                                })
                                                .executes(this::permissionSet))))
                        .then(Commands.literal("unset")
                                .then(Commands.argument("command", StringArgumentType.string())
                                        .suggests((context, suggestions) -> {
                                            Bukkit.getCommandMap().getKnownCommands().values().stream()
                                                    .map(org.bukkit.command.Command::getLabel)
                                                    .filter(s -> s.contains(suggestions.getRemaining()))
                                                    .map(StringArgumentType::escapeIfRequired)
                                                    .forEach(suggestions::suggest);
                                            return suggestions.buildFuture();
                                        })
                                        .executes(this::permissionUnset))))
                .then(Commands.literal("register")
                        .then(Commands.argument("command", StringArgumentType.string())
                                .suggests((context, suggestions) -> {
                                    plugin.commandRegistry().unregisteredCommands().stream()
                                            .filter(s -> s.contains(suggestions.getRemaining()))
                                            .map(StringArgumentType::escapeIfRequired)
                                            .forEach(suggestions::suggest);
                                    return suggestions.buildFuture();
                                })
                                .executes(this::register)))
                .then(Commands.literal("reset")
                        .then(Commands.argument("command", StringArgumentType.string())
                                .suggests((context, suggestions) -> {
                                    plugin.commandRegistry().hiddenCommands().stream()
                                            .filter(s -> s.contains(suggestions.getRemaining()))
                                            .map(StringArgumentType::escapeIfRequired)
                                            .forEach(suggestions::suggest);
                                    plugin.commandRegistry().unregisteredCommands().stream()
                                            .filter(s -> s.contains(suggestions.getRemaining()))
                                            .map(StringArgumentType::escapeIfRequired)
                                            .forEach(suggestions::suggest);
                                    return suggestions.buildFuture();
                                })
                                .executes(this::reset)))
                .then(Commands.literal("reveal")
                        .then(Commands.argument("command", StringArgumentType.string())
                                .suggests((context, suggestions) -> {
                                    plugin.commandRegistry().hiddenCommands().stream()
                                            .map(StringArgumentType::escapeIfRequired)
                                            .filter(s -> s.contains(suggestions.getRemaining()))
                                            .forEach(suggestions::suggest);
                                    return suggestions.buildFuture();
                                })
                                .executes(this::reveal)))
                .then(Commands.literal("unregister")
                        .then(Commands.argument("command", StringArgumentType.string())
                                .suggests((context, suggestions) -> {
                                    Bukkit.getCommandMap().getKnownCommands().values().stream()
                                            .map(org.bukkit.command.Command::getLabel)
                                            .filter(s -> !plugin.commandRegistry().isUnregistered(s))
                                            .filter(s -> s.contains(suggestions.getRemaining()))
                                            .map(StringArgumentType::escapeIfRequired)
                                            .forEach(suggestions::suggest);
                                    return suggestions.buildFuture();
                                })
                                .executes(this::unregister)))
                .build();
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(event ->
                event.registrar().register(command)));
    }

    private int hide(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var command = context.getArgument("command", String.class);
        var success = plugin.commandRegistry().hide(command);
        var message = success ? "command.hidden" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message, Placeholder.parsed("command", command));
        if (success) Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
        return Command.SINGLE_SUCCESS;
    }

    private int permissionQuery(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var command = context.getArgument("command", String.class);
        var registered = Bukkit.getCommandMap().getKnownCommands().get(command);
        var permission = registered != null ? registered.getPermission() : null;
        var message = registered == null ? "command.unknown" : permission != null ?
                "permission.query.defined" : "permission.query.undefined";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.parsed("permission", String.valueOf(permission)),
                Placeholder.parsed("command", command));
        return Command.SINGLE_SUCCESS;
    }

    private int permissionReset(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var command = context.getArgument("command", String.class);
        var success = plugin.permissionOverride().reset(command);
        var message = success ? "permission.reset" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message, Placeholder.parsed("command", command));
        if (success) Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
        return Command.SINGLE_SUCCESS;
    }

    private int permissionSet(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var command = context.getArgument("command", String.class);
        var permission = context.getArgument("permission", String.class);
        var success = plugin.permissionOverride().override(command, permission);
        var message = success ? "permission.set" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.parsed("permission", permission),
                Placeholder.parsed("command", command));
        if (success) Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
        return Command.SINGLE_SUCCESS;
    }

    private int permissionUnset(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var command = context.getArgument("command", String.class);
        var success = plugin.permissionOverride().override(command, null);
        var message = success ? "permission.set" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.parsed("permission", "null"),
                Placeholder.parsed("command", command));
        if (success) Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
        return Command.SINGLE_SUCCESS;
    }

    private int register(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var command = context.getArgument("command", String.class);
        var success = plugin.commandRegistry().register(command);
        var message = success ? "command.registered" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message, Placeholder.parsed("command", command));
        if (success) Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
        return Command.SINGLE_SUCCESS;
    }

    private int reset(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var command = context.getArgument("command", String.class);
        var s1 = plugin.permissionOverride().reset(command);
        var s3 = plugin.commandRegistry().register(command);
        var s2 = plugin.commandRegistry().reveal(command);
        var message = s1 || s2 || s3 ? "command.reset" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message, Placeholder.parsed("command", command));
        return Command.SINGLE_SUCCESS;
    }

    private int reveal(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var command = context.getArgument("command", String.class);
        var success = plugin.commandRegistry().reveal(command);
        var message = success ? "command.revealed" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message, Placeholder.parsed("command", command));
        if (success) Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
        return Command.SINGLE_SUCCESS;
    }

    private int unregister(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var command = context.getArgument("command", String.class);
        var success = plugin.commandRegistry().unregister(command);
        var message = success ? "command.unregistered" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message, Placeholder.parsed("command", command));
        if (success) Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
        return Command.SINGLE_SUCCESS;
    }
}
