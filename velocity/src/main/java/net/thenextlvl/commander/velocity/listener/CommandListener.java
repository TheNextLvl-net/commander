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
import net.thenextlvl.commander.CommandRegistry;
import net.thenextlvl.commander.PermissionOverride;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CommandListener {
    @Subscribe(priority = -1)
    public void onCommandSend(PlayerAvailableCommandsEvent event) {
        if (event.getPlayer().getPermissionValue("commander.bypass").equals(Tristate.TRUE)) return;
        var commandRegistry = CommandRegistry.instance();
        var permissionOverride = PermissionOverride.instance();
        event.getRootNode().getChildren().removeIf(commandNode -> {
            if (commandRegistry.isHidden(commandNode.getName())) return true;
            var permission = permissionOverride.permission(commandNode.getName());
            return permission != null && !event.getPlayer().hasPermission(permission);
        });
    }

    @Subscribe(priority = -1)
    public void onPlayerChat(CommandExecuteEvent event) {
        if (!event.getResult().isAllowed()) return;
        var command = event.getCommand().split(" ", 2)[0];
        if (event.getCommandSource() instanceof ConsoleCommandSource) return;
        var permission = PermissionOverride.instance().permission(command);
        if (permission == null || event.getCommandSource().hasPermission(permission)) return;
        event.getCommandSource().sendMessage(Component.translatable("command.unknown.command").appendNewline()
                .append(Component.text().append(Component.text(event.getCommand()).decorate(TextDecoration.UNDERLINED))
                        .append(Component.translatable("command.context.here").decorate(TextDecoration.ITALIC))
                        .clickEvent(ClickEvent.suggestCommand("/" + event.getCommand())))
                .color(NamedTextColor.RED));
        event.setResult(CommandExecuteEvent.CommandResult.denied());
    }
}
