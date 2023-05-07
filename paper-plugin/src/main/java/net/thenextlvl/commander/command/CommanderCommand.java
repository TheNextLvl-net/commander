package net.thenextlvl.commander.command;

import core.annotation.MethodsReturnNonnullByDefault;
import core.annotation.ParametersAreNonnullByDefault;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.commander.api.Commander;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CommanderCommand implements TabExecutor {
    private final Commander commander;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("permission")) return permission(sender, args);
        if (args.length >= 1 && args[0].equalsIgnoreCase("unregister")) return unregister(sender, args);
        if (args.length >= 1 && args[0].equalsIgnoreCase("register")) return register(sender, args);
        return true;
    }

    private boolean permission(CommandSender sender, String[] args) {
        return true;
    }

    private boolean unregister(CommandSender sender, String[] args) {
        return true;
    }

    private boolean register(CommandSender sender, String[] args) {
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length <= 1) {
            suggestions.add("permission");
            suggestions.add("unregister");
            suggestions.add("register");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("unregister")) {
                suggestions.addAll(Bukkit.getCommandMap().getKnownCommands().keySet());
            } else if (args[0].equalsIgnoreCase("register")) {
                suggestions.addAll(commander.commandManager().getRemovedCommands().getRoot());
            } else if (args[0].equalsIgnoreCase("permission")) {
                suggestions.add("reset");
                suggestions.add("set");
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("permission")) {
                if (args[1].equalsIgnoreCase("reset") || args[1].equalsIgnoreCase("set")) {
                    suggestions.addAll(commander.permissionManager().getPermissionOverride().getRoot().keySet());
                }
            }
        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("permission")) {
                if (args[1].equalsIgnoreCase("set")) {
                    suggestions.addAll(Bukkit.getPluginManager().getPermissions()
                            .stream().map(Permission::getName).toList());
                }
            }
        }
        suggestions.removeIf(token -> !token.toLowerCase().startsWith(args[args.length - 1].toLowerCase()));
        return suggestions;
    }
}
