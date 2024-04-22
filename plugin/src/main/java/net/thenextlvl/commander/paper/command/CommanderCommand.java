package net.thenextlvl.commander.paper.command;

import lombok.Getter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.api.CommandInfo;
import net.thenextlvl.commander.paper.CommanderPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class CommanderCommand extends Command implements PluginIdentifiableCommand {
    private final CommanderPlugin plugin;

    public CommanderCommand(CommanderPlugin plugin) {
        super("command", "Manage the commands on your server",
                "/command unregister | register | hide | reveal | permission | reset", List.of());
        setPermission("commander.admin");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length >= 1 && args[0].equals("permission")) permission(sender, args);
        else if (args.length >= 1 && args[0].equals("unregister")) unregister(sender, args);
        else if (args.length >= 1 && args[0].equals("register")) register(sender, args);
        else if (args.length >= 1 && args[0].equals("hide")) hide(sender, args);
        else if (args.length >= 1 && args[0].equals("reveal")) reveal(sender, args);
        else if (args.length >= 1 && args[0].equals("reset")) reset(sender, args);
        else notifySyntax(sender, getUsage());
        return true;
    }

    private void reset(CommandSender sender, String[] args) {
        if (args.length != 2) {
            notifySyntax(sender, "/command reset [command]");
            return;
        }
        var s1 = plugin.commander().commandRegistry().resetPermissions(args[1]);
        var s2 = plugin.commander().commandRegistry().registerCommand(args[1]);
        var s3 = plugin.commander().commandRegistry().revealCommand(args[1]);
        var message = s1 || s2 || s3 ? "command.reset" : "nothing.changed";
        plugin.commander().bundle().sendMessage(sender, message, Placeholder.parsed("command", args[1]));
    }

    private void permission(CommandSender sender, String[] args) {
        if (args.length >= 2 && args[1].equals("set")) permissionSet(sender, args);
        else if (args.length >= 2 && args[1].equals("reset")) permissionReset(sender, args);
        else if (args.length >= 2 && args[1].equals("query")) permissionQuery(sender, args);
        else notifySyntax(sender, "/command permission set | reset | query");
    }

    private void permissionSet(CommandSender sender, String[] args) {
        if (args.length != 4) {
            notifySyntax(sender, "/command permission set [command] [permission]");
            return;
        }
        var permission = args[3].equals("null") ? null : args[3];
        var success = plugin.commander().commandRegistry().overridePermissions(args[2], permission);
        var message = success ? "permission.set" : "nothing.changed";
        plugin.commander().bundle().sendMessage(sender, message,
                Placeholder.parsed("permission", String.valueOf(permission)),
                Placeholder.parsed("command", args[2]));
        if (success) plugin.commander().commandManager().updateCommands();
    }

    private void permissionReset(CommandSender sender, String[] args) {
        if (args.length != 3) {
            notifySyntax(sender, "/command permission reset [command]");
            return;
        }
        var success = plugin.commander().commandRegistry().resetPermissions(args[2]);
        var message = success ? "permission.reset" : "nothing.changed";
        plugin.commander().bundle().sendMessage(sender, message, Placeholder.parsed("command", args[2]));
        if (success) plugin.commander().commandManager().updateCommands();
    }

    private void permissionQuery(CommandSender sender, String[] args) {
        if (args.length != 3) {
            notifySyntax(sender, "/command permission query [command]");
            return;
        }
        var command = Bukkit.getCommandMap().getCommand(args[2]);
        var permission = command != null ? command.getPermission() : null;
        var message = command == null ? "command.unknown" : permission != null ?
                "permission.query.defined" : "permission.query.undefined";
        plugin.commander().bundle().sendMessage(sender, message,
                Placeholder.parsed("permission", String.valueOf(permission)),
                Placeholder.parsed("command", args[2]));
    }

    private void unregister(CommandSender sender, String[] args) {
        if (args.length == 2) try {
            if (args[1].contains("*")) CommandInfo.compile(args[1]);
            var success = plugin.commander().commandRegistry().registerCommandInfo(CommandInfo.remove(args[1]));
            var message = success ? "command.unregistered" : "nothing.changed";
            plugin.commander().bundle().sendMessage(sender, message, Placeholder.parsed("command", args[1]));
            if (success) plugin.commander().commandManager().updateCommands();
        } catch (Exception e) {
            plugin.commander().bundle().sendMessage(sender, "query.invalid", Placeholder.parsed("query", args[1]));
        }
        else notifySyntax(sender, "/command unregister [command]");
    }

    private void register(CommandSender sender, String[] args) {
        if (args.length != 2) {
            notifySyntax(sender, "/command register [command]");
            return;
        }
        var success = plugin.commander().commandRegistry().registerCommand(args[1]);
        var message = success ? "command.registered" : "nothing.changed";
        plugin.commander().bundle().sendMessage(sender, message, Placeholder.parsed("command", args[1]));
        if (success) plugin.commander().commandManager().updateCommands();
    }

    private void hide(CommandSender sender, String[] args) {
        if (args.length == 2) try {
            if (args[1].contains("*")) CommandInfo.compile(args[1]);
            var success = plugin.commander().commandRegistry().registerCommandInfo(CommandInfo.hide(args[1]));
            var message = success ? "command.hidden" : "nothing.changed";
            plugin.commander().bundle().sendMessage(sender, message, Placeholder.parsed("command", args[1]));
            if (success) plugin.commander().commandManager().updateCommands();
        } catch (Exception e) {
            plugin.commander().bundle().sendMessage(sender, "query.invalid", Placeholder.parsed("query", args[1]));
        }
        else notifySyntax(sender, "/command hide [command]");
    }

    private void reveal(CommandSender sender, String[] args) {
        if (args.length != 2) {
            notifySyntax(sender, "/command reveal [command]");
            return;
        }
        var success = plugin.commander().commandRegistry().revealCommand(args[1]);
        var message = success ? "command.revealed" : "nothing.changed";
        plugin.commander().bundle().sendMessage(sender, message, Placeholder.parsed("command", args[1]));
        if (success) plugin.commander().commandManager().updateCommands();
    }

    private void notifySyntax(CommandSender sender, String message) {
        plugin.commander().bundle().sendRawMessage(sender, "<prefix> <red>" + message
                .replace("[", "<dark_gray>[<gold>")
                .replace("]", "<dark_gray>]")
                .replace("|", "<dark_gray>|<red>"));
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        var suggestions = new ArrayList<String>();
        if (args.length <= 1) {
            suggestions.add("permission");
            suggestions.add("unregister");
            suggestions.add("register");
            suggestions.add("reveal");
            suggestions.add("reset");
            suggestions.add("hide");
        } else if (args.length == 2) {
            suggestions.addAll(switch (args[0]) {
                case "reset" -> plugin.commander().commandRegistry().getCommandInformation().stream()
                        .map(CommandInfo::query)
                        .toList();
                case "unregister" -> plugin.commander().commandManager().getCommandNames()
                        .filter(literal -> !plugin.commander().commandRegistry().isRemoved(literal))
                        .toList();
                case "register" -> plugin.commander().commandRegistry().getCommandInformation().stream()
                        .filter(CommandInfo::isRemoved)
                        .map(CommandInfo::query)
                        .toList();
                case "hide" -> plugin.commander().commandManager().getCommandNames()
                        .filter(literal -> !plugin.commander().commandRegistry().hasStatus(literal))
                        .toList();
                case "reveal" -> plugin.commander().commandRegistry().getCommandInformation().stream()
                        .filter(CommandInfo::isHidden)
                        .map(CommandInfo::query)
                        .toList();
                case "permission" -> List.of("reset", "set", "query");
                default -> Collections.emptyList();
            });
        } else if (args.length == 3) {
            if (args[0].equals("permission")) {
                if (args[1].equals("set") || args[1].equals("query")) suggestions.addAll(plugin.commander()
                        .commandManager().getCommandNames()
                        .filter(entry -> !plugin.commander().commandRegistry().isRemoved(entry))
                        .toList());
                else if (args[1].equals("reset")) suggestions.addAll(plugin.commander().commandRegistry()
                        .getCommandInformation().stream()
                        .filter(info -> info.permission() != null)
                        .map(CommandInfo::query)
                        .toList());
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
