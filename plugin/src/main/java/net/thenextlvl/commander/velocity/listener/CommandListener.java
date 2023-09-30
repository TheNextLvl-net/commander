package net.thenextlvl.commander.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.command.PlayerAvailableCommandsEvent;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.commander.api.CommandInfo;
import net.thenextlvl.commander.velocity.implementation.ProxyCommander;

@RequiredArgsConstructor
public class CommandListener {
    private final ProxyCommander commander;

    @Subscribe
    @SuppressWarnings("UnstableApiUsage")
    public void onCommandSend(PlayerAvailableCommandsEvent event) {
        event.getRootNode().getChildren().removeIf(commandNode -> commander.commandRegistry()
                .containsCommandInfo(info -> isHidden(event.getPlayer(), commandNode.getName(), info)));
    }

    private boolean isHidden(Player player, String literal, CommandInfo info) {
        return info.status() != null && info.nameMatches(literal) &&
                (!info.isHidden() || !player.getPermissionValue("commander.bypass").equals(Tristate.TRUE));
    }

    @Subscribe
    public void onCommand(CommandExecuteEvent event) {
        var literal = event.getCommand().split(" ")[0];
        if (!commander.commandRegistry().isRemoved(literal)) return;
        event.setResult(CommandExecuteEvent.CommandResult.forwardToServer());
    }
}
