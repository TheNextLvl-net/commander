package net.thenextlvl.commander.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.command.PlayerAvailableCommandsEvent;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
        var noSlash = event.getCommand().replaceFirst("/", "");
        var command = noSlash.split(" ", 2)[0];
        if (event.getCommandSource() instanceof ConsoleCommandSource) return;
        var permission = commander.commons.permissionOverride().permission(command);
        if (permission == null || event.getCommandSource().hasPermission(permission)) return;
        event.getCommandSource().sendMessage(Component.translatable("command.unknown.command").appendNewline()
                .append(Component.text().append(Component.text(noSlash).decorate(TextDecoration.UNDERLINED))
                        .append(Component.translatable("command.context.here").decorate(TextDecoration.ITALIC))
                        .clickEvent(ClickEvent.suggestCommand(event.getCommand())))
                .color(NamedTextColor.RED));
        event.setResult(CommandExecuteEvent.CommandResult.denied());
    }
}
