package net.thenextlvl.commander.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.implementation.ProxyCommander;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class CommanderCommand implements SimpleCommand {
    private final ProxyCommander commander;

    @Override
    public void execute(Invocation invocation) {
        var source = invocation.source();
        var args = invocation.arguments();
        if (args.length >= 1 && args[0].equals("unregister")) unregister(source, args);
        else if (args.length >= 1 && args[0].equals("register")) register(source, args);
        else notifySyntax(source, "register | unregister");
    }

    private void unregister(CommandSource sender, String[] args) {
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

    private void register(CommandSource source, String[] args) {
        if (args.length != 2) {
            notifySyntax(source, "register [command]");
            return;
        }
        var command = args[1];
        var success = commander.commandRegistry().registerCommand(command);
        var message = success ? "command.registered" : "nothing.changed";
        commander.bundle().sendMessage(source, message, Placeholder.parsed("command", command));
        commander.platform().commandRegistry().updateCommands();
    }

    private void notifySyntax(CommandSource source, String message) {
        commander.bundle().sendRawMessage(source, "%prefix% <red>/v-command " + message
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
        } else if (args.length == 2) {
            suggestions.addAll(switch (args[0]) {
                case "unregister" -> commander.platform().commandRegistry().getCommandNamespaces()
                        .filter(name -> !commander.commandRegistry().isCommandRemoved(name))
                        .toList();
                case "register" -> commander.commandRegistry().getRemovedCommands();
                default -> Collections.emptyList();
            });
        }
        return suggestions;
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("commander.admin");
    }
}
