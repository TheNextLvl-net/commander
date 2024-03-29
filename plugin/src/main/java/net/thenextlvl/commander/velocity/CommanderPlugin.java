package net.thenextlvl.commander.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.thenextlvl.commander.velocity.listener.CommandListener;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.thenextlvl.commander.velocity.command.CommanderCommand;
import net.thenextlvl.commander.velocity.implementation.ProxyCommander;
import org.slf4j.Logger;

import java.nio.file.Path;

@Getter
@Accessors(fluent = true)
@Plugin(id = "commander",
        name = "Commander",
        authors = "NonSwag",
        url = "https://thenextlvl.net",
        version = "3.0.4")
public class CommanderPlugin {
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataFolder;

    @Inject
    public CommanderPlugin(ProxyServer server, Logger logger, @DataDirectory Path dataFolder) {
        this.server = server;
        this.logger = logger;
        this.dataFolder = dataFolder;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        var commander = new ProxyCommander(this);
        server().getEventManager().register(this, new CommandListener(commander));
        server().getCommandManager().register("v-command", new CommanderCommand(commander));
    }
}
