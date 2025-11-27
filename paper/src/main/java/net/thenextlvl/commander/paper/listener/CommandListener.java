package net.thenextlvl.commander.paper.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.util.TriState;
import net.thenextlvl.commander.CommandRegistry;
import net.thenextlvl.commander.PermissionOverride;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CommandListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandSend(PlayerCommandSendEvent event) {
        if (event.getPlayer().permissionValue("commander.bypass").equals(TriState.TRUE)) return;
        event.getCommands().removeAll(CommandRegistry.instance().hiddenCommands());
        var permissionOverride = PermissionOverride.instance();
        event.getCommands().removeIf(command -> {
            var permission = permissionOverride.permission(command);
            return permission != null && !event.getPlayer().hasPermission(permission);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        var noSlash = event.getMessage().substring(1);
        var command = noSlash.split(" ", 2)[0];
        var permission = PermissionOverride.instance().permission(command);
        if (permission == null || event.getPlayer().hasPermission(permission)) return;
        event.getPlayer().sendMessage(Component.translatable("command.unknown.command").appendNewline()
                .append(Component.text().append(Component.text(noSlash).decorate(TextDecoration.UNDERLINED))
                        .append(Component.translatable("command.context.here").decorate(TextDecoration.ITALIC))
                        .clickEvent(ClickEvent.suggestCommand(event.getMessage())))
                .color(NamedTextColor.RED));
        event.setCancelled(true);
    }
}
