package net.thenextlvl.commander.implementation;

import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.proxy.Player;
import core.annotation.MethodsReturnNotNullByDefault;
import core.i18n.file.ComponentBundle;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.commander.CommanderPlugin;
import net.thenextlvl.commander.api.CommandRegistry;
import net.thenextlvl.commander.api.Commander;
import net.thenextlvl.commander.api.platform.PermissionManager;

import java.io.File;
import java.util.Locale;

@Getter
@Accessors(fluent = true)
@MethodsReturnNotNullByDefault
public class ProxyCommander implements Commander<CommandMeta> {
    private final ComponentBundle bundle;
    private final CommandRegistry commandRegistry;
    private final ProxyCommandManager commandManager;

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
        commandRegistry = new CommandRegistry(this, plugin.dataFolder().toFile());
        commandManager = new ProxyCommandManager(this, plugin);
    }

    @Override
    public PermissionManager<CommandMeta> permissionManager() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported on the proxy");
    }
}