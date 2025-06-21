package net.thenextlvl.commander.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import net.kyori.adventure.audience.Audience;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import core.i18n.file.ComponentBundle;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.thenextlvl.commander.CommandFinder;
import net.thenextlvl.commander.Commander;
import net.thenextlvl.commander.velocity.command.CommanderCommand;
import net.thenextlvl.commander.velocity.implementation.ProxyCommandFinder;
import net.thenextlvl.commander.velocity.implementation.ProxyCommandRegistry;
import net.thenextlvl.commander.velocity.implementation.ProxyPermissionOverride;
import net.thenextlvl.commander.velocity.listener.CommandListener;
import net.thenextlvl.commander.velocity.version.CommanderVersionChecker;
import org.bstats.velocity.Metrics;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.Locale;

@NullMarked
@Plugin(id = "commander",
        name = "Commander",
        authors = "NonSwag",
        url = "https://thenextlvl.net",
        version = "4.3.3")
public class CommanderPlugin implements Commander {
    public static final String ROOT_COMMAND = "commandv";
    private final ComponentBundle bundle;
    private final ProxyCommandFinder commandFinder;
    private final ProxyCommandRegistry commandRegistry;
    private final ProxyPermissionOverride permissionOverride;
    private final Metrics.Factory metricsFactory;
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataFolder;

    @Inject
    public CommanderPlugin(ProxyServer server, Logger logger, @DataDirectory Path dataFolder, Metrics.Factory metricsFactory) {
        this.server = server;
        this.logger = logger;
        this.dataFolder = dataFolder;
        this.metricsFactory = metricsFactory;
        var key = Key.key("commander", "translations");
        var translations = dataFolder.resolve("translations");
        this.bundle = ComponentBundle.builder(key, translations)
                .placeholder("prefix", "prefix")
                .resource("commander.properties", Locale.US)
                .resource("commander_german.properties", Locale.GERMANY)
                .build();
        this.commandFinder = new ProxyCommandFinder(this);
        this.commandRegistry = new ProxyCommandRegistry(this);
        this.permissionOverride = new ProxyPermissionOverride(this);
        new CommanderVersionChecker(this).checkVersion();
    }

    @Subscribe(priority = -1)
    public void onProxyInitialize(ProxyInitializeEvent event) {
        metricsFactory.make(this, 22782);
        server().getEventManager().register(this, new CommandListener(this));
        var meta = server.getCommandManager().metaBuilder("commandv").plugin(this).build();
        server().getCommandManager().register(meta, CommanderCommand.create(this));
        commandRegistry().unregisterCommands();
    }

    @Subscribe(priority = 999)
    public void onProxyShutdown(ProxyShutdownEvent event) {
        commandRegistry.save(true);
        permissionOverride.save(true);
    }

    @Override
    public CommandFinder commandFinder() {
        return commandFinder;
    }

    @Override
    public ComponentBundle bundle() {
        return bundle;
    }

    @Override
    public ProxyCommandRegistry commandRegistry() {
        return commandRegistry;
    }

    @Override
    public ProxyPermissionOverride permissionOverride() {
        return permissionOverride;
    }

    public String rootCommand() {
        return ROOT_COMMAND;
    }

    public void autoSave(Audience audience) {
        var savedRegistry = commandRegistry.save(false);
        var savedPermissions = permissionOverride.save(false);
        if (!savedRegistry || !savedPermissions) {
            var mm = MiniMessage.miniMessage();
            var serialized = mm.serialize(bundle.component("command.save.conflict", audience));
            serialized = serialized.replace("{ROOTCMD}", ROOT_COMMAND);
            audience.sendMessage(mm.deserialize(serialized));
        }
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

