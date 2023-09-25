package net.thenextlvl.commander.implementation;

import com.velocitypowered.api.proxy.Player;
import core.i18n.file.ComponentBundle;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.commander.CommanderPlugin;
import net.thenextlvl.commander.api.Commander;
import net.thenextlvl.commander.implementation.command.ProxyCommandRegistry;
import net.thenextlvl.commander.implementation.command.ProxyPlatformCommandRegistry;
import net.thenextlvl.commander.implementation.permission.ProxyPermissionRegistry;
import net.thenextlvl.commander.implementation.permission.ProxyPlatformPermissionRegistry;

import java.io.File;
import java.util.Locale;

@Getter
@Accessors(fluent = true)
public class ProxyCommander implements Commander {
    private final ComponentBundle bundle;
    private final ProxyCommandRegistry commandRegistry;
    private final ProxyPermissionRegistry permissionRegistry;
    private final CraftPlatformRegistry platform;
    private final CommanderPlugin plugin;

    public ProxyCommander(CommanderPlugin plugin) {
        bundle = new ComponentBundle(new File(plugin.dataFolder().toFile(), "translations"), audience ->
                audience instanceof Player player ? player.getPlayerSettings().getLocale() : Locale.US)
                .register("commander", Locale.US)
                .register("commander_german", Locale.GERMANY)
                .fallback(Locale.US);
        bundle().miniMessage(MiniMessage.builder().tags(TagResolver.resolver(
                TagResolver.standard(),
                Placeholder.parsed("prefix", bundle().format(Locale.US, "prefix"))
        )).build());
        commandRegistry = new ProxyCommandRegistry(this, plugin);
        permissionRegistry = new ProxyPermissionRegistry(this, plugin);
        platform = new CraftPlatformRegistry();
        this.plugin = plugin;
    }

    @Getter
    @Accessors(fluent = true)
    public class CraftPlatformRegistry implements PlatformRegistry {
        private final ProxyPlatformCommandRegistry commandRegistry;
        private final ProxyPlatformPermissionRegistry permissionRegistry;

        private CraftPlatformRegistry() {
            commandRegistry = new ProxyPlatformCommandRegistry(ProxyCommander.this);
            permissionRegistry = new ProxyPlatformPermissionRegistry(ProxyCommander.this);
        }
    }
}
