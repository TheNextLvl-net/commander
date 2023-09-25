package net.thenextlvl.commander.implementation;

import core.i18n.file.ComponentBundle;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.commander.api.Commander;
import net.thenextlvl.commander.implementation.command.CraftCommandRegistry;
import net.thenextlvl.commander.implementation.command.CraftPlatformCommandRegistry;
import net.thenextlvl.commander.implementation.permission.CraftPermissionRegistry;
import net.thenextlvl.commander.implementation.permission.CraftPlatformPermissionRegistry;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Locale;

@Getter
@Accessors(fluent = true)
public class CraftCommander implements Commander {
    private final ComponentBundle bundle;
    private final CraftCommandRegistry commandRegistry;
    private final CraftPermissionRegistry permissionRegistry;
    private final CraftPlatformRegistry platform;

    public CraftCommander(File dataFolder) {
        bundle = new ComponentBundle(new File(dataFolder, "translations"), audience ->
                audience instanceof Player player ? player.locale() : Locale.US)
                .register("commander", Locale.US)
                .register("commander_german", Locale.GERMANY)
                .fallback(Locale.US);
        bundle().miniMessage(MiniMessage.builder().tags(TagResolver.resolver(
                TagResolver.standard(),
                Placeholder.parsed("prefix", bundle().format(Locale.US, "prefix"))
        )).build());
        commandRegistry = new CraftCommandRegistry(this, dataFolder);
        permissionRegistry = new CraftPermissionRegistry(this, dataFolder);
        platform = new CraftPlatformRegistry();
    }

    @Getter
    @Accessors(fluent = true)
    public class CraftPlatformRegistry implements PlatformRegistry {
        private final CraftPlatformCommandRegistry commandRegistry;
        private final CraftPlatformPermissionRegistry permissionRegistry;

        private CraftPlatformRegistry() {
            commandRegistry = new CraftPlatformCommandRegistry(CraftCommander.this);
            permissionRegistry = new CraftPlatformPermissionRegistry(CraftCommander.this);
        }
    }
}
