package net.thenextlvl.commander.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.faststats.ErrorTracker;
import dev.faststats.velocity.VelocityContext;
import net.thenextlvl.commander.command.CommanderCommand;
import net.thenextlvl.commander.velocity.listener.CommandListener;
import net.thenextlvl.commander.velocity.version.CommanderVersionChecker;
import org.bstats.velocity.Metrics;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;

import java.nio.file.Path;

@NullMarked
@Plugin(id = "commander",
        name = "Commander",
        authors = "NonSwag",
        url = "https://thenextlvl.net",
        version = "5.1.0")
public class CommanderPlugin {
    public static final ErrorTracker ERROR_TRACKER = ErrorTracker.contextAware();

    private final VelocityContext context;
    private final Metrics.Factory bStats;
    private final ProxyServer server;
    private final Logger logger;
    private final ProxyCommander commons;
    private final Path dataPath;

    @Inject
    public CommanderPlugin(
            final ProxyServer server, final Logger logger, @DataDirectory final Path dataPath,
            final Metrics.Factory bStats, final VelocityContext.Factory context
    ) {
        this.server = server;
        this.logger = logger;
        this.dataPath = dataPath;
        this.commons = new ProxyCommander(this);
        this.bStats = bStats;
        this.context = context.token("417c37aa7e3b468fc09ee54af4336490")
                .metrics(dev.faststats.Metrics.Factory::create)
                .errorTrackerService(ERROR_TRACKER)
                .create();
        new CommanderVersionChecker(this).checkVersion();
    }

    @Subscribe(priority = -1)
    public void onProxyInitialize(final ProxyInitializeEvent event) {
        context.ready();
        bStats.make(this, 22782);
        server().getEventManager().register(this, new CommandListener());
        final var meta = server.getCommandManager().metaBuilder(commons.getRootCommand()).plugin(this).build();
        server().getCommandManager().register(meta, new BrigadierCommand(CommanderCommand.create(commons)));
        commons.commandRegistry().unregisterCommands();
    }

    @Subscribe(priority = 999)
    public void onProxyShutdown(final ProxyShutdownEvent event) {
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
