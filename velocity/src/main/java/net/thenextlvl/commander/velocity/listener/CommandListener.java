package net.thenextlvl.commander.velocity.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.PlayerAvailableCommandsEvent;
import com.velocitypowered.api.permission.Tristate;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.commander.velocity.CommanderPlugin;

@RequiredArgsConstructor
public class CommandListener {
    private final CommanderPlugin commander;

    @Subscribe(order = PostOrder.LAST)
    @SuppressWarnings("UnstableApiUsage")
    public void onCommandSend(PlayerAvailableCommandsEvent event) {
        if (event.getPlayer().getPermissionValue("commander.bypass").equals(Tristate.TRUE)) return;
        event.getRootNode().getChildren().removeIf(commandNode -> {
            if (commander.commandRegistry().isHidden(commandNode.getName())) return true;
            var permission = commander.permissionOverride().permission(commandNode.getName());
            return !event.getPlayer().hasPermission(permission);
        });
    }
}
