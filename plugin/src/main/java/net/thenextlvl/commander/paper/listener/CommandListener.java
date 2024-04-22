package net.thenextlvl.commander.paper.listener;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.util.TriState;
import net.thenextlvl.commander.api.CommandInfo;
import net.thenextlvl.commander.paper.CommanderPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.command.UnknownCommandEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.ServerCommandEvent;

@RequiredArgsConstructor
public class CommandListener implements Listener {
    private final CommanderPlugin plugin;

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommandSend(PlayerCommandSendEvent event) {
        event.getCommands().removeIf(literal -> plugin.commander().commandRegistry()
                .containsCommandInfo(info -> isHidden(event.getPlayer(), literal, info)));
    }

    private boolean isHidden(Player player, String literal, CommandInfo info) {
        return info.status() != null && info.nameMatches(literal) &&
               (!info.isHidden() || !player.permissionValue("commander.bypass").equals(TriState.TRUE));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommand(ServerCommandEvent event) {
        var literal = event.getCommand().split(" ")[0];
        if (plugin.commander().commandManager().isCommandRegistered(literal)
            && !plugin.commander().commandRegistry().isRemoved(literal)) return;
        plugin.commander().bundle().sendMessage(event.getSender(), "command.unknown",
                Placeholder.parsed("command", literal));
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onUnknownCommand(UnknownCommandEvent event) {
        var literal = event.getCommandLine().split(" ")[0];
        if (plugin.commander().commandManager().isCommandRegistered(literal)) return;
        event.message(plugin.commander().bundle().component(event.getSender(), "command.unknown",
                Placeholder.parsed("command", literal)));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        var player = event.getPlayer();
        var literal = event.getMessage().substring(1).split(" ")[0];
        var command = plugin.commander().commandManager().getCommand(literal).orElse(null);
        if (command == null || plugin.commander().commandRegistry().isRemoved(literal)) {
            event.setCancelled(true);
            if (literal.isBlank()) return;
            plugin.commander().bundle().sendMessage(player, "command.unknown",
                    Placeholder.parsed("command", literal));
        } else if (!command.testPermissionSilent(player)) {
            var permission = command.getPermission() != null ? command.getPermission() : getPermission(literal);
            if (permission != null) plugin.commander().bundle().sendMessage(player, "command.permission",
                    Placeholder.parsed("permission", permission),
                    Placeholder.parsed("command", literal));
            else plugin.commander().bundle().sendMessage(player, "command.permission.unknown",
                    Placeholder.parsed("command", literal));
            event.setCancelled(true);
        }
    }

    private String getPermission(String literal) {
        return plugin.commander().commandRegistry().getCommandInformation(literal)
                .map(CommandInfo::permission)
                .orElse(null);
    }
}
