package net.thenextlvl.commander.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import net.thenextlvl.binder.StaticBinder;
import net.thenextlvl.commander.CommandFinder;
import net.thenextlvl.commander.CommandRegistry;
import net.thenextlvl.commander.CommanderCommons;
import net.thenextlvl.commander.PermissionOverride;
import net.thenextlvl.commander.access.BrigadierAccess;
import net.thenextlvl.commander.velocity.access.ProxyBrigadierAccess;
import net.thenextlvl.commander.velocity.implementation.ProxyCommandFinder;
import net.thenextlvl.commander.velocity.implementation.ProxyCommandRegistry;
import net.thenextlvl.commander.velocity.implementation.ProxyPermissionOverride;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;

import java.util.stream.Stream;

@NullMarked
public class ProxyCommander extends CommanderCommons {
    private final ProxyBrigadierAccess brigadierAccess = new ProxyBrigadierAccess();

    private final ProxyCommandFinder commandFinder;
    private final ProxyCommandRegistry commandRegistry;
    private final ProxyPermissionOverride permissionOverride;

    private final CommanderPlugin plugin;

    public ProxyCommander(CommanderPlugin plugin) {
        super(plugin.dataPath());
        this.plugin = plugin;
        this.commandFinder = new ProxyCommandFinder(this);
        this.commandRegistry = new ProxyCommandRegistry(this);
        this.permissionOverride = new ProxyPermissionOverride(this);
        StaticBinder.getInstance(CommandFinder.class.getClassLoader()).bind(CommandFinder.class, commandFinder);
        StaticBinder.getInstance(CommandRegistry.class.getClassLoader()).bind(CommandRegistry.class, commandRegistry);
        StaticBinder.getInstance(PermissionOverride.class.getClassLoader()).bind(PermissionOverride.class, permissionOverride);
    }

    public ProxyServer server() {
        return plugin.server();
    }

    @Override
    public Logger logger() {
        return plugin.logger();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S> BrigadierAccess<S> brigadierAccess() {
        return (BrigadierAccess<S>) brigadierAccess;
    }

    @Override
    public String getRootCommand() {
        return "commandv";
    }

    @Override
    public Stream<String> getKnownCommands() {
        return Stream.empty();
    }

    @Override
    public Stream<String> getKnownPermissions() {
        return Stream.empty();
    }

    @Override
    public void updateCommands() {
    }

    @Override
    public ProxyCommandFinder commandFinder() {
        return commandFinder;
    }

    @Override
    public ProxyCommandRegistry commandRegistry() {
        return commandRegistry;
    }

    @Override
    public ProxyPermissionOverride permissionOverride() {
        return permissionOverride;
    }
}
