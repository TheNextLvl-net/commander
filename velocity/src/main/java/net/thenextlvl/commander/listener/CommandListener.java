package net.thenextlvl.commander.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.PlayerAvailableCommandsEvent;
import com.velocitypowered.api.permission.Tristate;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.commander.CommanderPlugin;

@RequiredArgsConstructor
public class CommandListener {
    private final CommanderPlugin commander;

    @Subscribe
    @SuppressWarnings("UnstableApiUsage")
    public void onCommandSend(PlayerAvailableCommandsEvent event) {
        if (event.getPlayer().getPermissionValue("commander.bypass").equals(Tristate.TRUE)) return;
        event.getRootNode().getChildren().removeIf(commandNode ->
                commander.commandRegistry().isHidden(commandNode.getName()));
    }
}
