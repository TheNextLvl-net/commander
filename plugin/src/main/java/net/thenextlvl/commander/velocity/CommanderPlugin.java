package net.thenextlvl.commander.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import core.i18n.file.ComponentBundle;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.commander.api.CommandRegistry;
import net.thenextlvl.commander.api.Commander;
import net.thenextlvl.commander.api.PermissionOverride;
import net.thenextlvl.commander.velocity.implementation.ProxyCommandRegistry;
import net.thenextlvl.commander.velocity.listener.CommandListener;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.thenextlvl.commander.velocity.command.CommanderCommand;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.Locale;

@Getter
@Accessors(fluent = true)
@Plugin(id = "commander",
        name = "Commander",
        authors = "NonSwag",
        url = "https://thenextlvl.net",
        version = "3.0.4")
public class CommanderPlugin implements Commander {
    private final ComponentBundle bundle;
    private final CommandRegistry commandRegistry;
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataFolder;

    @Inject
    public CommanderPlugin(ProxyServer server, Logger logger, @DataDirectory Path dataFolder) {
        this.server = server;
        this.logger = logger;
        this.dataFolder = dataFolder;
        this.bundle = new ComponentBundle(new File(dataFolder.toFile(), "translations"), audience ->
                audience instanceof Player player ? player.getPlayerSettings().getLocale() : Locale.US)
                .register("commander", Locale.US)
                .register("commander_german", Locale.GERMANY)
                .miniMessage(bundle -> MiniMessage.builder().tags(TagResolver.resolver(
                        TagResolver.standard(),
                        Placeholder.component("prefix", bundle.component(Locale.US, "prefix"))
                )).build());
        this.commandRegistry = new ProxyCommandRegistry(this);
    }

    @Override
    public PermissionOverride permissionOverride() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported on the proxy");
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        server().getEventManager().register(this, new CommandListener(this));
        server().getCommandManager().register("v-command", new CommanderCommand(this));
    }
}
