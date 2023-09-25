package net.thenextlvl.commander.command;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.implementation.CraftCommander;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
        else notifySyntax(sender, getUsage());
        return true;
    }

    private void permission(CommandSender sender, String[] args) {
        if (args.length >= 2 && args[1].equals("set")) permissionSet(sender, args);
        else if (args.length >= 2 && args[1].equals("reset")) permissionReset(sender, args);
        else if (args.length >= 2 && args[1].equals("query")) permissionQuery(sender, args);
        else notifySyntax(sender, "permission set | reset | query");
    }

    private void permissionSet(CommandSender sender, String[] args) {
        if (args.length == 4) {
            var command = args[2];
            var permission = args[3].equals("null") ? null : args[3];
            commander.permissionRegistry().overridePermission(command, permission);
            commander.bundle().sendMessage(sender, "permission.set",
                    Placeholder.parsed("permission", String.valueOf(permission)),
                    Placeholder.parsed("command", command));
            commander.platform().commandRegistry().updateCommands();
        } else notifySyntax(sender, "permission set [command] [permission]");
    }

    private void permissionReset(CommandSender sender, String[] args) {
        if (args.length == 3) {
            var command = args[2];
            var success = commander.permissionRegistry().resetPermission(command);
            var message = success ? "permission.reset" : "nothing.changed";
            commander.bundle().sendMessage(sender, message, Placeholder.parsed("command", command));
            if (success) commander.platform().commandRegistry().updateCommands();
        } else notifySyntax(sender, "permission reset [command]");
    }

    private void permissionQuery(CommandSender sender, String[] args) {
        if (args.length != 3) {
            notifySyntax(sender, "permission query [command]");
            return;
        }
        var command = Bukkit.getCommandMap().getCommand(args[2]);
        var permission = command != null ? command.getPermission() : null;
        var message = command == null ? "command.unknown" : permission != null ?
                "permission.query.defined" : "permission.query.undefined";
        commander.bundle().sendMessage(sender, message,
                Placeholder.parsed("permission", String.valueOf(permission)),
                Placeholder.parsed("command", args[2]));
    }

    private void unregister(CommandSender sender, String[] args) {
        if (args.length == 2) try {
            if (args[1].contains("*")) Pattern.compile(args[1].replaceAll("\\*", ".+"));
            var success = commander.commandRegistry().unregisterCommands(args[1]);
            var message = success ? "command.unregistered" : "nothing.changed";
            commander.bundle().sendMessage(sender, message, Placeholder.parsed("command", args[1]));
            if (success) commander.platform().commandRegistry().updateCommands();
        } catch (Exception e) {
            commander.bundle().sendMessage(sender, "query.invalid", Placeholder.parsed("query", args[1]));
        }
        else notifySyntax(sender, "unregister [command]");
    }

    private void register(CommandSender sender, String[] args) {
        if (args.length != 2) {
            notifySyntax(sender, "register [command]");
            return;
        }
        var command = args[1];
        var success = commander.commandRegistry().registerCommand(command);
        var message = success ? "command.registered" : "nothing.changed";
        commander.bundle().sendMessage(sender, message, Placeholder.parsed("command", command));
        commander.platform().commandRegistry().updateCommands();
    }

    private void notifySyntax(CommandSender sender, String message) {
        commander.bundle().sendRawMessage(sender, "%prefix% <red>/command " + message
                .replace("[", "<dark_gray>[<gold>")
                .replace("]", "<dark_gray>]")
                .replace("|", "<dark_gray>|<red>"));
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
