package net.thenextlvl.commander.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.audience.Audience;
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
        version = "5.0.0")
public class CommanderPlugin {
    private final Metrics.Factory metricsFactory;
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataFolder;

    public final ProxyCommander commons = new ProxyCommander(this); // todo: weaken visibility

    @Inject
    public CommanderPlugin(ProxyServer server, Logger logger, @DataDirectory Path dataFolder, Metrics.Factory metricsFactory) {
        this.server = server;
        this.logger = logger;
        this.dataFolder = dataFolder;
        this.metricsFactory = metricsFactory;
        new CommanderVersionChecker(this).checkVersion();
    }

    @Subscribe(priority = -1)
    public void onProxyInitialize(ProxyInitializeEvent event) {
        metricsFactory.make(this, 22782);
        server().getEventManager().register(this, new CommandListener(this));
        var meta = server.getCommandManager().metaBuilder(commons.getRootCommand()).plugin(this).build();
        server().getCommandManager().register(meta, new BrigadierCommand(CommanderCommand.create(commons)));
        commons.commandRegistry().unregisterCommands();
    }

    @Subscribe(priority = 999)
    public void onProxyShutdown(ProxyShutdownEvent event) {
        commons.commandRegistry().save(true);
        commons.permissionOverride().save(true);
    }

    public void conflictSave(Audience audience) {
        if (commons.commandRegistry().save(false) & commons.permissionOverride().save(false)) return;
        commons.bundle().sendMessage(audience, "command.save.conflict");
    }

    public void hiddenConflictSave(Audience audience) {
        if (commons.commandRegistry().saveHidden(false)) return;
        commons.bundle().sendMessage(audience, "command.save.conflict");
    }

    public void unregisteredConflictSave(Audience audience) {
        if (commons.commandRegistry().saveUnregistered(false)) return;
        commons.bundle().sendMessage(audience, "command.save.conflict");
    }

    public void permissionConflictSave(Audience audience) {
        if (commons.permissionOverride().save(false)) return;
        commons.bundle().sendMessage(audience, "command.save.conflict");
    }

    public ProxyServer server() {
        return server;
    }

    public Logger logger() {
        return logger;
    }

    public Path dataFolder() {
        return dataFolder;
    }
}
