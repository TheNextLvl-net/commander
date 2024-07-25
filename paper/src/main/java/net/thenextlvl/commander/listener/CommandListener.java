package net.thenextlvl.commander.listener;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.util.TriState;
import net.thenextlvl.commander.api.Commander;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

@RequiredArgsConstructor
public class CommandListener implements Listener {
    private final Commander commander;

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommandSend(PlayerCommandSendEvent event) {
        if (event.getPlayer().permissionValue("commander.bypass").equals(TriState.TRUE)) return;
        event.getCommands().removeAll(commander.commandRegistry().hiddenCommands());
    }
}
