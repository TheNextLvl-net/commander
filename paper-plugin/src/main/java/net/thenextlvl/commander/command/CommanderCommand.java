package net.thenextlvl.commander.command;

import core.api.placeholder.Placeholder;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.commander.i18n.Messages;
import net.thenextlvl.commander.implementation.CraftCommander;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@SuppressWarnings("removal")
public class CommanderCommand implements TabExecutor {
    private final CraftCommander commander;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
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
            commander.permissionRegistry().overridePermission(command, permission);
            var locale = sender instanceof Player player ? player.locale() : Locale.US;
            sender.sendRichMessage(Messages.PERMISSION_SET.message(locale, sender,
                    Placeholder.of("permission", permission),
                    Placeholder.of("command", command)));
            commander.platform().commandRegistry().updateCommands();
        } else sendCorrectSyntax(sender, "/command permission set [command] [permission]");
    }

    private void permissionReset(CommandSender sender, String[] args) {
        if (args.length == 3) {
            var command = args[2];
            var success = commander.permissionRegistry().resetPermission(command);
            var locale = sender instanceof Player player ? player.locale() : Locale.US;
            var message = success ? Messages.PERMISSION_RESET : Messages.NOTHING_CHANGED;
            sender.sendRichMessage(message.message(locale, sender, Placeholder.of("command", command)));
            if (success) commander.platform().commandRegistry().updateCommands();
        } else sendCorrectSyntax(sender, "/command permission reset [command]");
    }

    private void permissionQuery(CommandSender sender, String[] args) {
        if (args.length == 3) {
            var command = Bukkit.getCommandMap().getCommand(args[2]);
            var permission = command != null ? command.getPermission() : null;
            var locale = sender instanceof Player player ? player.locale() : Locale.US;
            var message = command == null ? Messages.UNKNOWN_COMMAND : permission != null ?
                    Messages.PERMISSION_QUERY_DEFINED : Messages.PERMISSION_QUERY_UNDEFINED;
            sender.sendRichMessage(message.message(locale, sender,
                    Placeholder.of("permission", permission),
                    Placeholder.of("command", args[2])));
        } else sendCorrectSyntax(sender, "/command permission query [command]");
    }

    private void unregister(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sendCorrectSyntax(sender, "/command unregister [command]");
            return;
        }
        var locale = sender instanceof Player player ? player.locale() : Locale.US;
        try {
            if (args[1].contains("*")) Pattern.compile(args[1].replaceAll("\\*", ".+"));
            var success = commander.commandRegistry().unregisterCommands(args[1]);
            var message = success ? Messages.COMMAND_UNREGISTERED : Messages.NOTHING_CHANGED;
            sender.sendRichMessage(message.message(locale, sender, Placeholder.of("command", args[1])));
            if (success) commander.platform().commandRegistry().updateCommands();
        } catch (Exception e) {
            sender.sendRichMessage(Messages.INVALID_QUERY.message(locale, sender, Placeholder.of("query", args[1])));
        }
    }

    private void register(CommandSender sender, String[] args) {
        if (args.length == 2) {
            var command = args[1];
            var success = commander.commandRegistry().registerCommand(command);
            var message = success ? Messages.COMMAND_REGISTERED : Messages.NOTHING_CHANGED;
            var locale = sender instanceof Player player ? player.locale() : Locale.US;
            sender.sendRichMessage(message.message(locale, sender, Placeholder.of("command", command)));
            commander.platform().commandRegistry().updateCommands();
        } else sendCorrectSyntax(sender, "/command register [command]");
    }

    private void sendCorrectSyntax(CommandSender sender, String message) {
        sender.sendRichMessage(Messages.FORMATTER.format("%prefix% <red>" + message
                .replace("[", "<dark_gray>[<gold>").replace("]", "<dark_gray>]")
                .replace("|", "<dark_gray>|<red>")
        ));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length <= 1) {
            suggestions.add("permission");
            suggestions.add("unregister");
            suggestions.add("register");
        } else if (args.length == 2) {
            suggestions.addAll(switch (args[0]) {
                case "unregister" -> commander.platform().commandRegistry().getCommandNamespaces()
                        .filter(name -> !commander.commandRegistry().isCommandRemoved(name))
                        .toList();
                case "register" -> commander.commandRegistry().getRemovedCommands();
                case "permission" -> List.of("reset", "set", "query");
                default -> Collections.emptyList();
            });
        } else if (args.length == 3) {
            if (args[0].equals("permission")) {
                if (args[1].equals("reset") || args[1].equals("set") || args[1].equals("query")) {
                    suggestions.addAll(commander.permissionRegistry().getPermissionOverride().keySet());
                }
                if (args[1].equals("set") || args[1].equals("query")) {
                    suggestions.addAll(Bukkit.getCommandMap().getKnownCommands().keySet().stream()
                            .filter(entry -> !commander.commandRegistry().isCommandRemoved(entry))
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
