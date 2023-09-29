package net.thenextlvl.commander.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.api.CommandInfo;
import net.thenextlvl.commander.implementation.ProxyCommander;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class CommanderCommand implements SimpleCommand {
    private final ProxyCommander commander;

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
        var s1 = commander.commandRegistry().registerCommand(args[1]);
        var s2 = commander.commandRegistry().revealCommand(args[1]);
        var message = s1 || s2 ? "command.reset" : "nothing.changed";
        commander.bundle().sendMessage(source, message, Placeholder.parsed("command", args[1]));
    }

    private void unregister(CommandSource source, String[] args) {
        if (args.length == 2) try {
            if (args[1].contains("*")) CommandInfo.compile(args[1]);
            var success = commander.commandRegistry().registerCommandInfo(CommandInfo.remove(args[1]));
            var message = success ? "command.unregistered" : "nothing.changed";
            commander.bundle().sendMessage(source, message, Placeholder.parsed("command", args[1]));
            if (success) commander.commandManager().updateCommands();
        } catch (Exception e) {
            commander.bundle().sendMessage(source, "query.invalid", Placeholder.parsed("query", args[1]));
        }
        else notifySyntax(source, "/v-command unregister [command]");
    }

    private void register(CommandSource source, String[] args) {
        if (args.length != 2) {
            notifySyntax(source, "/v-command register [command]");
            return;
        }
        var success = commander.commandRegistry().registerCommand(args[1]);
        var message = success ? "command.registered" : "nothing.changed";
        commander.bundle().sendMessage(source, message, Placeholder.parsed("command", args[1]));
        if (success) commander.commandManager().updateCommands();
    }

    private void hide(CommandSource source, String[] args) {
        if (args.length == 2) try {
            if (args[1].contains("*")) CommandInfo.compile(args[1]);
            var success = commander.commandRegistry().registerCommandInfo(CommandInfo.hide(args[1]));
            var message = success ? "command.hidden" : "nothing.changed";
            commander.bundle().sendMessage(source, message, Placeholder.parsed("command", args[1]));
            if (success) commander.commandManager().updateCommands();
        } catch (Exception e) {
            commander.bundle().sendMessage(source, "query.invalid", Placeholder.parsed("query", args[1]));
        }
        else notifySyntax(source, "/v-command hide [command]");
    }

    private void reveal(CommandSource source, String[] args) {
        if (args.length != 2) {
            notifySyntax(source, "/v-command reveal [command]");
            return;
        }
        var success = commander.commandRegistry().revealCommand(args[1]);
        var message = success ? "command.revealed" : "nothing.changed";
        commander.bundle().sendMessage(source, message, Placeholder.parsed("command", args[1]));
        if (success) commander.commandManager().updateCommands();
    }

    private void notifySyntax(CommandSource source, String message) {
        commander.bundle().sendRawMessage(source, "<prefix> <red>" + message
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
                case "reset" -> commander.commandRegistry().getCommandInformation().stream()
                        .map(CommandInfo::query)
                        .toList();
                case "unregister" -> commander.commandManager().getCommandNames()
                        .filter(literal -> !commander.commandRegistry().isRemoved(literal))
                        .toList();
                case "register" -> commander.commandRegistry().getCommandInformation().stream()
                        .filter(CommandInfo::isRemoved)
                        .map(CommandInfo::query)
                        .toList();
                case "hide" -> commander.commandManager().getCommandNames()
                        .filter(literal -> !commander.commandRegistry().hasStatus(literal))
                        .toList();
                case "reveal" -> commander.commandRegistry().getCommandInformation().stream()
                        .filter(CommandInfo::isHidden)
                        .map(CommandInfo::query)
                        .toList();
                default -> Collections.emptyList();
            });
        }
        suggestions.removeIf(token -> !token.toLowerCase().contains(args[args.length - 1].toLowerCase()));
        return suggestions;
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("commander.admin");
    }
}
