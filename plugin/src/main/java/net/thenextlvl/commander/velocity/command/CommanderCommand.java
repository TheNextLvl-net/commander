package net.thenextlvl.commander.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.velocity.CommanderPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class CommanderCommand implements SimpleCommand {
    private final CommanderPlugin plugin;

    @Override
    public void execute(Invocation invocation) {
        var source = invocation.source();
        var args = invocation.arguments();
        if (args.length >= 1 && args[0].equals("unregister")) unregister(source, args);
        else if (args.length >= 1 && args[0].equals("register")) register(source, args);
        else if (args.length >= 1 && args[0].equals("hide")) hide(source, args);
        else if (args.length >= 1 && args[0].equals("reveal")) reveal(source, args);
        else if (args.length >= 1 && args[0].equals("reset")) reset(source, args);
        else notifySyntax(source, "/v-command unregister | register | hide | reveal | reset");
    }

    private void reset(CommandSource source, String[] args) {
        if (args.length != 2) {
            notifySyntax(source, "/v-command reset [command]");
            return;
        }
        var s1 = plugin.commandRegistry().register(args[1]);
        var s2 = plugin.commandRegistry().reveal(args[1]);
        var message = s1 || s2 ? "command.reset" : "nothing.changed";
        plugin.bundle().sendMessage(source, message, Placeholder.parsed("command", args[1]));
    }

    private void unregister(CommandSource source, String[] args) {
        if (args.length == 2) try {
            var success = plugin.commandRegistry().unregister(args[1]);
            var message = success ? "command.unregistered" : "nothing.changed";
            plugin.bundle().sendMessage(source, message, Placeholder.parsed("command", args[1]));
        } catch (Exception e) {
            plugin.bundle().sendMessage(source, "query.invalid", Placeholder.parsed("query", args[1]));
        }
        else notifySyntax(source, "/v-command unregister [command]");
    }

    private void register(CommandSource source, String[] args) {
        if (args.length != 2) {
            notifySyntax(source, "/v-command register [command]");
            return;
        }
        var success = plugin.commandRegistry().register(args[1]);
        var message = success ? "command.registered" : "nothing.changed";
        plugin.bundle().sendMessage(source, message, Placeholder.parsed("command", args[1]));
    }

    private void hide(CommandSource source, String[] args) {
        if (args.length == 2) try {
            var success = plugin.commandRegistry().hide(args[1]);
            var message = success ? "command.hidden" : "nothing.changed";
            plugin.bundle().sendMessage(source, message, Placeholder.parsed("command", args[1]));
        } catch (Exception e) {
            plugin.bundle().sendMessage(source, "query.invalid", Placeholder.parsed("query", args[1]));
        }
        else notifySyntax(source, "/v-command hide [command]");
    }

    private void reveal(CommandSource source, String[] args) {
        if (args.length != 2) {
            notifySyntax(source, "/v-command reveal [command]");
            return;
        }
        var success = plugin.commandRegistry().reveal(args[1]);
        var message = success ? "command.revealed" : "nothing.changed";
        plugin.bundle().sendMessage(source, message, Placeholder.parsed("command", args[1]));
    }

    private void notifySyntax(CommandSource source, String message) {
        plugin.bundle().sendRawMessage(source, "<prefix> <red>" + message
                .replace("[", "<dark_gray>[<gold>")
                .replace("]", "<dark_gray>]")
                .replace("|", "<dark_gray>|<red>"));
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        var args = invocation.arguments();
        var suggestions = new ArrayList<String>();
        if (args.length <= 1) {
            suggestions.add("unregister");
            suggestions.add("register");
            suggestions.add("reveal");
            suggestions.add("reset");
            suggestions.add("hide");
        } else if (args.length == 2) {
            suggestions.addAll(switch (args[0]) {
                case "reset" -> {
                    var all = new ArrayList<String>();
                    all.addAll(plugin.commandRegistry().hiddenCommands());
                    all.addAll(plugin.commandRegistry().unregisteredCommands());
                    yield all;
                }
                case "unregister" -> plugin.server().getCommandManager().getAliases();
                case "register" -> plugin.commandRegistry().unregisteredCommands();
                case "hide" -> {
                    var all = new ArrayList<>(plugin.server().getCommandManager().getAliases());
                    all.removeAll(plugin.commandRegistry().hiddenCommands());
                    yield all;
                }
                case "reveal" -> plugin.commandRegistry().hiddenCommands();
                default -> Collections.emptyList();
            });
        }
        return args.length > 0 ? suggestions.stream()
                .filter((name) -> {
                    var currentArg = args[args.length - 1];
                    return name.regionMatches(true, 0, currentArg, 0, currentArg.length());
                })
                .toList() : suggestions;
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("commander.admin");
    }
}
