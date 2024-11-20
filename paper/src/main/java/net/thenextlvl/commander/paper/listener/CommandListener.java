package net.thenextlvl.commander.paper.listener;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.util.TriState;
import net.thenextlvl.commander.paper.CommanderPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.command.UnknownCommandEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
@RequiredArgsConstructor
public class CommandListener implements Listener {
    private final CommanderPlugin plugin;

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommandSend(PlayerCommandSendEvent event) {
        if (event.getPlayer().permissionValue("commander.bypass").equals(TriState.TRUE)) return;
        event.getCommands().removeAll(plugin.commandRegistry().hiddenCommands());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onUnknownCommand(UnknownCommandEvent event) {
        if (!plugin.config().getRoot().unknownCommandMessage()) return;
        var split = event.getCommandLine().split(" ", 2);
        var command = plugin.getServer().getCommandMap().getCommand(split[0]);
        if (command != null
            && !event.getCommandLine().equals(split[0])
            && command.testPermissionSilent(event.getSender()))
            return;
        event.message(plugin.bundle().component(event.getSender(), "command.unknown"));
    }
}
