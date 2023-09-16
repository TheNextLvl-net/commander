package net.thenextlvl.commander.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.command.PlayerAvailableCommandsEvent;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.commander.implementation.ProxyCommander;

@RequiredArgsConstructor
public class CommandListener {
    private final ProxyCommander commander;

    @Subscribe
    @SuppressWarnings("UnstableApiUsage")
    public void onCommandSend(PlayerAvailableCommandsEvent event) {
        event.getRootNode().getChildren().removeIf(commandNode ->
                commander.commandRegistry().isCommandRemoved(commandNode.getName()));
    }

    @Subscribe
    public void onCommand(CommandExecuteEvent event) {
        var literal = event.getCommand().split(" ")[0];
        if (!commander.commandRegistry().isCommandRemoved(literal)) return;
        event.setResult(CommandExecuteEvent.CommandResult.forwardToServer());
    }
}
