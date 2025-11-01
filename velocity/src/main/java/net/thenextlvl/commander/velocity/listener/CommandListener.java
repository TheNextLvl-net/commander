package net.thenextlvl.commander.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.command.PlayerAvailableCommandsEvent;
import com.velocitypowered.api.permission.Tristate;
import net.thenextlvl.commander.velocity.CommanderPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CommandListener {
    private final CommanderPlugin commander;

    public CommandListener(CommanderPlugin commander) {
        this.commander = commander;
    }

    @Subscribe(priority = -1)
    public void onCommandSend(PlayerAvailableCommandsEvent event) {
        if (event.getPlayer().getPermissionValue("commander.bypass").equals(Tristate.TRUE)) return;
        event.getRootNode().getChildren().removeIf(commandNode -> {
            if (commander.commons.commandRegistry().isHidden(commandNode.getName())) return true;
            var permission = commander.commons.permissionOverride().permission(commandNode.getName());
            return permission != null && !event.getPlayer().hasPermission(permission);
        });
    }

    @Subscribe(priority = -1)
    public void onPlayerChat(CommandExecuteEvent event) {
        if (!event.getResult().isAllowed()) return;
        var command = event.getCommand().replaceFirst("/", "").stripLeading();
        var permission = commander.commons.permissionOverride().permission(command);
        if (permission == null || event.getCommandSource().hasPermission(permission)) return;
        event.setResult(CommandExecuteEvent.CommandResult.forwardToServer());
    }
}
