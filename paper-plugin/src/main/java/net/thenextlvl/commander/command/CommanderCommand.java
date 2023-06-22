package net.thenextlvl.commander.command;

import core.annotation.MethodsReturnNonnullByDefault;
import core.annotation.ParametersAreNonnullByDefault;
import core.api.placeholder.Placeholder;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.commander.api.Commander;
import net.thenextlvl.commander.i18n.Messages;
import net.thenextlvl.commander.i18n.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CommanderCommand implements TabExecutor {
    private final Commander commander;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1 && args[0].equals("permission")) permission(sender, args);
        else if (args.length >= 1 && args[0].equals("unregister")) unregister(sender, args);
        else if (args.length >= 1 && args[0].equals("register")) register(sender, args);
        else sendCorrectSyntax(sender, command.getUsage());
        return true;
    }

    private void permission(CommandSender sender, String[] args) {
        if (args.length >= 2 && args[1].equals("set")) permissionSet(sender, args);
        else if (args.length >= 2 && args[1].equals("reset")) permissionReset(sender, args);
        else if (args.length >= 2 && args[1].equals("query")) permissionQuery(sender, args);
        else sendCorrectSyntax(sender, "/command permission set | reset | query");
    }

    private void permissionSet(CommandSender sender, String[] args) {
        if (args.length == 4) {
            var command = args[2];
            var permission = args[3].equals("null") ? null : args[3];
            commander.permissionManager().getPermissionOverride().getRoot().put(command, permission);
            var success = commander.permissionManager().overridePermission(command, permission);
            commander.permissionManager().getPermissionOverride().save();
            var locale = sender instanceof Player player ? player.locale() : Messages.ENGLISH;
            sender.sendRichMessage(Messages.PERMISSION_SET.message(locale, sender,
                    Placeholder.of("permission", permission),
                    Placeholder.of("command", command)));
            if (success) Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
        } else sendCorrectSyntax(sender, "/command permission set [command] [permission]");
    }

    private void permissionReset(CommandSender sender, String[] args) {
        if (args.length == 3) {
            var command = args[2];
            var success = commander.permissionManager().resetPermission(command);
            var locale = sender instanceof Player player ? player.locale() : Messages.ENGLISH;
            var message = success ? Messages.PERMISSION_RESET : Messages.NOTHING_CHANGED;
            sender.sendRichMessage(message.message(locale, sender, Placeholder.of("command", command)));
            if (success) Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
        } else sendCorrectSyntax(sender, "/command permission reset [command]");
    }

    private void permissionQuery(CommandSender sender, String[] args) {
        if (args.length == 3) {
            var command = Bukkit.getCommandMap().getCommand(args[2]);
            var permission = command != null ? command.getPermission() : null;
            var locale = sender instanceof Player player ? player.locale() : Messages.ENGLISH;
            var message = command == null ? Messages.UNKNOWN_COMMAND : permission != null ?
                    Messages.PERMISSION_QUERY_DEFINED : Messages.PERMISSION_QUERY_UNDEFINED;
            sender.sendRichMessage(message.message(locale, sender,
                    Placeholder.of("permission", permission),
                    Placeholder.of("command", args[2])));
        } else sendCorrectSyntax(sender, "/command permission query [command]");
    }

    private void unregister(CommandSender sender, String[] args) {
        if (args.length == 2) {
            var commands = commander.commandManager().getRemovedCommands();
            boolean contains = commands.getRoot().contains(args[1]);
            if (!contains) {
                commands.getRoot().add(args[1]);
                commands.save();
            }
            var message = contains ? Messages.NOTHING_CHANGED : Messages.COMMAND_UNREGISTERED;
            var locale = sender instanceof Player player ? player.locale() : Messages.ENGLISH;
            sender.sendRichMessage(message.message(locale, sender, Placeholder.of("command", args[1])));
            if (!contains) sender.sendRichMessage(Messages.RESTART_REQUIRED.message(locale, sender));
        } else sendCorrectSyntax(sender, "/command unregister [command]");
    }

    private void register(CommandSender sender, String[] args) {
        if (args.length == 2) {
            var command = args[1];
            var success = commander.commandManager().registerCommand(command);
            var message = success ? Messages.COMMAND_REGISTERED : Messages.NOTHING_CHANGED;
            var locale = sender instanceof Player player ? player.locale() : Messages.ENGLISH;
            sender.sendRichMessage(message.message(locale, sender, Placeholder.of("command", command)));
            if (success) sender.sendRichMessage(Messages.RESTART_REQUIRED.message(locale, sender));
        } else sendCorrectSyntax(sender, "/command register [command]");
    }

    private void sendCorrectSyntax(CommandSender sender, String message) {
        sender.sendRichMessage(Placeholders.FORMATTER.format("%prefix% <red>" + message
                .replace("[", "<dark_gray>[<gold>").replace("]", "<dark_gray>]")
                .replace("|", "<dark_gray>|<red>")
        ));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length <= 1) {
            suggestions.add("permission");
            suggestions.add("unregister");
            suggestions.add("register");
        } else if (args.length == 2) {
            suggestions.addAll(switch (args[0]) {
                case "unregister" -> Bukkit.getCommandMap().getKnownCommands().keySet().stream()
                        .filter(entry -> !commander.commandManager().isCommandUnregistered(entry))
                        .filter(entry -> entry.contains(":"))
                        .toList();
                case "register" -> commander.commandManager().getRemovedCommands().getRoot();
                case "permission" -> List.of("reset", "set", "query");
                default -> Collections.emptyList();
            });
        } else if (args.length == 3) {
            if (args[0].equals("permission")) {
                if (args[1].equals("reset") || args[1].equals("set") || args[1].equals("query")) {
                    suggestions.addAll(commander.permissionManager().getPermissionOverride().getRoot().keySet());
                }
                if (args[1].equals("set") || args[1].equals("query")) {
                    suggestions.addAll(Bukkit.getCommandMap().getKnownCommands().keySet().stream()
                            .filter(entry -> !commander.commandManager().isCommandUnregistered(entry))
                            .filter(entry -> entry.contains(":"))
                            .toList());
                }
            }
        } else if (args.length == 4) {
            if (args[0].equals("permission")) {
                if (args[1].equals("set")) {
                    suggestions.addAll(Bukkit.getPluginManager().getPermissions()
                            .stream().map(Permission::getName).toList());
                    suggestions.add("null");
                }
            }
        }
        suggestions.removeIf(token -> !token.toLowerCase().contains(args[args.length - 1].toLowerCase()));
        return suggestions;
    }
}
