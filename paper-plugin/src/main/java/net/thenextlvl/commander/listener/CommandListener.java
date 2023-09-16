package net.thenextlvl.commander.listener;

import core.api.placeholder.Placeholder;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.thenextlvl.commander.i18n.Messages;
import net.thenextlvl.commander.implementation.CraftCommander;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.command.UnknownCommandEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.Locale;

@RequiredArgsConstructor
@SuppressWarnings("removal")
public class CommandListener implements Listener {
    private final CraftCommander commander;

    @EventHandler(ignoreCancelled = true)
    public void onCommandSend(PlayerCommandSendEvent event) {
        event.getCommands().removeIf(literal -> commander.commandRegistry().isCommandRemoved(literal));
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommand(ServerCommandEvent event) {
        var literal = event.getCommand().split(" ")[0];
        if (commander.platform().commandRegistry().isCommandRegistered(literal)
                && !commander.commandRegistry().isCommandRemoved(literal)) return;
        var locale = event.getSender() instanceof Player player ? player.locale() : Locale.US;
        event.getSender().sendRichMessage(Messages.UNKNOWN_COMMAND.message(locale,
                event.getSender(), Placeholder.of("command", literal)));
        event.setCancelled(true);
    }

    @EventHandler
    public void onUnknownCommand(UnknownCommandEvent event) {
        var literal = event.getCommandLine().split(" ")[0];
        if (!commander.commandRegistry().isCommandRemoved(literal)) return;
        var locale = event.getSender() instanceof Player player ? player.locale() : Locale.US;
        event.message(MiniMessage.miniMessage().deserialize(Messages.UNKNOWN_COMMAND
                .message(locale, event.getSender(), Placeholder.of("command", literal))));
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        var player = event.getPlayer();
        var literal = event.getMessage().substring(1).split(" ")[0];
        var command = commander.platform().commandRegistry().getCommand(literal).orElse(null);
        if (command == null || commander.commandRegistry().isCommandRemoved(literal)) {
            event.setCancelled(true);
            if (literal.isBlank()) return;
            player.sendRichMessage(Messages.UNKNOWN_COMMAND.message(player.locale(), player,
                    Placeholder.of("command", literal)));
        } else if (!command.testPermissionSilent(player)) {
            player.sendRichMessage(Messages.NO_PERMISSION.message(player.locale(), player,
                    Placeholder.of("permission", command.getPermission() != null ? command.getPermission() : "unknown")));
            event.setCancelled(true);
        }
    }
}
