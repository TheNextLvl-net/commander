package net.thenextlvl.commander.paper.listener;

import net.kyori.adventure.util.TriState;
import net.thenextlvl.commander.paper.CommanderPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CommandListener implements Listener {
    private final CommanderPlugin plugin;

    public CommandListener(CommanderPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandSend(PlayerCommandSendEvent event) {
        if (event.getPlayer().permissionValue("commander.bypass").equals(TriState.TRUE)) return;
        event.getCommands().removeAll(plugin.commons.commandRegistry().hiddenCommands());
        event.getCommands().removeIf(command -> {
            var permission = plugin.commons.permissionOverride().permission(command);
            return permission != null && !event.getPlayer().hasPermission(permission);
        });
    }
}
