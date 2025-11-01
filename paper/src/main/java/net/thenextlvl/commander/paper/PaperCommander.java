package net.thenextlvl.commander.paper;

import net.kyori.adventure.audience.Audience;
import net.thenextlvl.commander.CommanderCommons;
import net.thenextlvl.commander.access.BrigadierAccess;
import net.thenextlvl.commander.paper.access.PaperBrigadierAccess;
import net.thenextlvl.commander.paper.implementation.PaperCommandFinder;
import net.thenextlvl.commander.paper.implementation.PaperCommandRegistry;
import net.thenextlvl.commander.paper.implementation.PaperPermissionOverride;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.stream.Stream;

@NullMarked
public class PaperCommander extends CommanderCommons {
    private final PaperBrigadierAccess brigadierAccess = new PaperBrigadierAccess();

    private final PaperCommandFinder commandFinder;
    private final PaperCommandRegistry commandRegistry;
    private final PaperPermissionOverride permissionOverride;

    private final CommanderPlugin plugin;

    public PaperCommander(CommanderPlugin plugin) {
        this.plugin = plugin;
        this.commandFinder = new PaperCommandFinder(plugin);
        this.commandRegistry = new PaperCommandRegistry(plugin);
        this.permissionOverride = new PaperPermissionOverride(plugin);
    }

    @Override
    public Logger logger() {
        return plugin.getSLF4JLogger();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S> BrigadierAccess<S> brigadierAccess() {
        return (BrigadierAccess<S>) brigadierAccess;
    }

    @Override
    public String getRootCommand() {
        return "commander";
    }

    @Override
    public Path getDataPath() {
        return plugin.getDataPath();
    }

    @Override
    public Stream<String> getKnownCommands() {
        return plugin.getServer().getCommandMap().getKnownCommands().values().stream()
                .map(Command::getLabel);
    }

    @Override
    public Stream<String> getKnownPermissions() {
        return plugin.getServer().getPluginManager().getPermissions().stream()
                .map(Permission::getName);
    }

    @Override
    public void updateCommands() {
        plugin.getServer().getOnlinePlayers().forEach(Player::updateCommands);
    }

    @Override
    public PaperCommandFinder commandFinder() {
        return commandFinder;
    }

    @Override
    public PaperCommandRegistry commandRegistry() {
        return commandRegistry;
    }

    @Override
    public PaperPermissionOverride permissionOverride() {
        return permissionOverride;
    }

    public void conflictSave(Audience audience) {
        if (commandRegistry().save(false) & permissionOverride().save(false)) return;
        bundle().sendMessage(audience, "command.save.conflict");
    }

    public void hiddenConflictSave(Audience audience) {
        if (commandRegistry().saveHidden(false)) return;
        bundle().sendMessage(audience, "command.save.conflict");
    }

    public void unregisteredConflictSave(Audience audience) {
        if (commandRegistry().saveUnregistered(false)) return;
        bundle().sendMessage(audience, "command.save.conflict");
    }

    public void permissionConflictSave(Audience audience) {
        if (permissionOverride().save(false)) return;
        bundle().sendMessage(audience, "command.save.conflict");
    }
}
