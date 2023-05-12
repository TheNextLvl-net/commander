package net.thenextlvl.commander.listener;

import core.annotation.ParametersAreNonnullByDefault;
import core.api.placeholder.Placeholder;
import net.thenextlvl.commander.i18n.Messages;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

@ParametersAreNonnullByDefault
public class CommandListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onCommand(ServerCommandEvent event) {
        var label = event.getCommand().split(" ")[0];
        if (commander.commandManager().isCommandRegistered(label)) return;
        event.setCancelled(true);
        var sender = event.getSender();
        sender.sendRichMessage(Messages.UNKNOWN_COMMAND.message(Messages.ENGLISH, sender,
                Placeholder.of("command", label)));
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        var player = event.getPlayer();
        var label = event.getMessage().substring(1).split(" ")[0];
        var command = Bukkit.getCommandMap().getCommand(label);
        if (command == null || !commander.commandManager().isCommandRegistered(label)) {
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
