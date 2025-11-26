package net.thenextlvl.commander.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.faststats.core.Metrics;
import dev.faststats.velocity.VelocityMetrics;
import net.thenextlvl.commander.command.CommanderCommand;
import net.thenextlvl.commander.velocity.listener.CommandListener;
import net.thenextlvl.commander.velocity.version.CommanderVersionChecker;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

@NullMarked
@Plugin(id = "commander",
        name = "Commander",
        authors = "NonSwag",
        url = "https://thenextlvl.net",
        version = "5.0.0")
public class CommanderPlugin {
    private final Metrics.Factory<Object> fastStats;
    private final org.bstats.velocity.Metrics.Factory bStats;
    private final ProxyServer server;
    private final Logger logger;
    private final ProxyCommander commons;
    private final Path dataPath;

    @Inject
    public CommanderPlugin(
            ProxyServer server, Logger logger, @DataDirectory Path dataPath,
            org.bstats.velocity.Metrics.Factory bStats, VelocityMetrics.Factory fastStats
    ) {
        this.server = server;
        this.logger = logger;
        this.dataPath = dataPath;
        this.commons = new ProxyCommander(this);
        this.bStats = bStats;
        this.fastStats = fastStats.token("651f3fbc8bfa16b7f98b6192e3a992");
        new CommanderVersionChecker(this).checkVersion();
    }

    @Subscribe(priority = -1)
    public void onProxyInitialize(ProxyInitializeEvent event) throws IOException {
        fastStats.create(this);
        bStats.make(this, 22782);
        server().getEventManager().register(this, new CommandListener());
        var meta = server.getCommandManager().metaBuilder(commons.getRootCommand()).plugin(this).build();
        server().getCommandManager().register(meta, new BrigadierCommand(CommanderCommand.create(commons)));
        commons.commandRegistry().unregisterCommands();
    }

    @Subscribe(priority = 999)
    public void onProxyShutdown(ProxyShutdownEvent event) {
        commons.commandRegistry().save(true);
        commons.permissionOverride().save(true);
    }

    public ProxyServer server() {
        return server;
    }

    public Logger logger() {
        return logger;
    }

    public Path dataPath() {
        return dataPath;
    }
}
