package net.thenextlvl.commander.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import core.api.placeholder.Placeholder;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.thenextlvl.commander.i18n.Messages;
import net.thenextlvl.commander.implementation.ProxyCommander;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@SuppressWarnings("removal")
public class CommanderCommand implements SimpleCommand {
    private final ProxyCommander commander;

    @Override
    public void execute(Invocation invocation) {
        var source = invocation.source();
        var args = invocation.arguments();
        if (args.length >= 1 && args[0].equals("unregister")) unregister(source, args);
        else if (args.length >= 1 && args[0].equals("register")) register(source, args);
        else sendCorrectSyntax(source, "/vcommand register | unregister");
    }

    private void unregister(CommandSource source, String[] args) {
        if (args.length != 2) {
            sendCorrectSyntax(source, "/command unregister [command]");
            return;
        }
        var locale = source instanceof Player player ? player.getPlayerSettings().getLocale() : Locale.US;
        try {
            if (args[1].contains("*")) Pattern.compile(args[1].replaceAll("\\*", ".+"));
            var success = commander.commandRegistry().unregisterCommands(args[1]);
            var message = success ? Messages.COMMAND_UNREGISTERED : Messages.NOTHING_CHANGED;
            source.sendMessage(MiniMessage.miniMessage().deserialize(
                    message.message(locale, source, Placeholder.of("command", args[1]))));
            if (success) commander.platform().commandRegistry().updateCommands();
        } catch (Exception e) {
            source.sendMessage(MiniMessage.miniMessage().deserialize(Messages.INVALID_QUERY
                    .message(locale, source, Placeholder.of("query", args[1]))));
        }
    }

    private void register(CommandSource sender, String[] args) {
        if (args.length == 2) {
            var command = args[1];
            var success = commander.commandRegistry().registerCommand(command);
            var message = success ? Messages.COMMAND_REGISTERED : Messages.NOTHING_CHANGED;
            var locale = sender instanceof Player player ? player.getPlayerSettings().getLocale() : Locale.US;
            sender.sendMessage(MiniMessage.miniMessage().deserialize(message.message(locale, sender,
                    Placeholder.of("command", command))));
            commander.platform().commandRegistry().updateCommands();
        } else sendCorrectSyntax(sender, "/command register [command]");
    }

    private void sendCorrectSyntax(CommandSource source, String message) {
        source.sendMessage(MiniMessage.miniMessage().deserialize(Messages.FORMATTER.format("%prefix% <red>" + message
                .replace("[", "<dark_gray>[<gold>").replace("]", "<dark_gray>]")
                .replace("|", "<dark_gray>|<red>")
        )));
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
