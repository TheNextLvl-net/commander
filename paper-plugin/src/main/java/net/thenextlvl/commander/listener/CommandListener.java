package net.thenextlvl.commander.listener;

import core.api.placeholder.Placeholder;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.thenextlvl.commander.api.Commander;
import net.thenextlvl.commander.i18n.Messages;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.command.UnknownCommandEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.Locale;

@RequiredArgsConstructor
public class CommandListener implements Listener {
    private final Commander commander;

    @EventHandler(ignoreCancelled = true)
    public void onCommandSend(PlayerCommandSendEvent event) {
        event.getCommands().removeIf(literal -> commander.commandRegistry().isCommandRemoved(literal));
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommand(ServerCommandEvent event) {
        var literal = event.getCommand().split(" ")[0];
        if (commander.platformCommandRegistry().isCommandRegistered(literal)
                || !commander.commandRegistry().isCommandRemoved(literal)) return;
        event.getSender().sendRichMessage(Messages.UNKNOWN_COMMAND.message(Locale.US,
                event.getSender(), Placeholder.of("command", literal)));
        event.setCancelled(true);
    }

    @EventHandler
    public void onUnknownCommand(UnknownCommandEvent event) {
        var literal = event.getCommandLine().split(" ")[0];
        if (!commander.commandRegistry().isCommandRemoved(literal)) return;
        event.message(MiniMessage.miniMessage().deserialize(Messages.UNKNOWN_COMMAND
                .message(Locale.US, event.getSender(), Placeholder.of("command", literal))));
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        var player = event.getPlayer();
        var label = event.getMessage().substring(1).split(" ")[0];
        var command = Bukkit.getCommandMap().getCommand(label);
        if (command == null || !commander.platformCommandRegistry().isCommandRegistered(label)) {
            event.setCancelled(true);
            if (label.isBlank()) return;
            player.sendRichMessage(Messages.UNKNOWN_COMMAND.message(player.locale(), player,
                    Placeholder.of("command", label)));
        } else if (!command.testPermissionSilent(player)) {
            event.setCancelled(true);
            player.sendRichMessage(Messages.NO_PERMISSION.message(player.locale(), player,
                    Placeholder.of("permission", command.getPermission())));
        }
    }
}
