package net.thenextlvl.commander.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import core.i18n.file.ComponentBundle;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
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

import java.io.File;
import java.nio.file.Path;
import java.util.Locale;

@Getter
@NullMarked
@Accessors(fluent = true)
@Plugin(id = "commander",
        name = "Commander",
        authors = "NonSwag",
        url = "https://thenextlvl.net",
        version = "4.1.0")
public class CommanderPlugin implements Commander {
    private final CommanderVersionChecker versionChecker = new CommanderVersionChecker(this);
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
        this.bundle = new ComponentBundle(new File(dataFolder.toFile(), "translations"), audience ->
                audience instanceof Player player ? player.getPlayerSettings().getLocale() : Locale.US)
                .register("commander", Locale.US)
                .register("commander_german", Locale.GERMANY)
                .miniMessage(bundle -> MiniMessage.builder().tags(TagResolver.resolver(
                        TagResolver.standard(),
                        Placeholder.component("prefix", bundle.component(Locale.US, "prefix"))
                )).build());
        this.commandFinder = new ProxyCommandFinder(this);
        this.commandRegistry = new ProxyCommandRegistry(this);
        this.permissionOverride = new ProxyPermissionOverride(this);
        versionChecker.checkVersion();
    }

    @SuppressWarnings("deprecation")
    @Subscribe(order = PostOrder.CUSTOM, priority = -1)
    public void onProxyInitialize(ProxyInitializeEvent event) {
        metricsFactory.make(this, 22782);
        server().getEventManager().register(this, new CommandListener(this));
        var meta = server.getCommandManager().metaBuilder("commandv").plugin(this).build();
        server().getCommandManager().register(meta, new CommanderCommand().create(this));
        commandRegistry().unregisterCommands();
    }

    @SuppressWarnings("deprecation")
    @Subscribe(order = PostOrder.CUSTOM, priority = 999)
    public void onProxyShutdown(ProxyShutdownEvent event) {
        commandRegistry.getHiddenFile().save();
        commandRegistry.getUnregisteredFile().save();
        permissionOverride.getOverridesFile().save();
    }
}
