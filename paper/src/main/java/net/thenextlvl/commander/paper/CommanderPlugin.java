package net.thenextlvl.commander.paper;

import core.i18n.file.ComponentBundle;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.commander.Commander;
import net.thenextlvl.commander.paper.command.CommanderCommand;
import net.thenextlvl.commander.paper.implementation.PaperCommandRegistry;
import net.thenextlvl.commander.paper.implementation.PaperPermissionOverride;
import net.thenextlvl.commander.paper.listener.CommandListener;
import net.thenextlvl.commander.paper.version.VersionChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Locale;
import java.util.Objects;

@Getter
@Accessors(fluent = true)
public class CommanderPlugin extends JavaPlugin implements Commander {
    private final Metrics metrics = new Metrics(this, 22782);
    private final VersionChecker versionChecker = new VersionChecker();

    private final File translations = new File(getDataFolder(), "translations");
    private final ComponentBundle bundle = new ComponentBundle(translations, audience ->
            audience instanceof Player player ? player.locale() : Locale.US)
            .register("commander", Locale.US)
            .register("commander_german", Locale.GERMANY)
            .miniMessage(bundle -> MiniMessage.builder().tags(TagResolver.resolver(
                    TagResolver.standard(),
                    Placeholder.component("prefix", bundle.component(Locale.US, "prefix"))
            )).build());

    private final PaperCommandRegistry commandRegistry = new PaperCommandRegistry(this);
    private final PaperPermissionOverride permissionOverride = new PaperPermissionOverride(this);

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void onLoad() {
        Bukkit.getServicesManager().register(Commander.class, this, this, ServicePriority.Highest);
        versionChecker.retrieveLatestSupportedVersion(latest -> latest.ifPresent(version -> {
            var running = VersionChecker.Version.parse(getPluginMeta().getVersion());
            if (version.equals(running)) {
                getComponentLogger().info("You are running the latest version of Commander");
            } else if (version.compareTo(Objects.requireNonNull(running)) > 0) {
                getComponentLogger().warn("An update for Commander is available");
                getComponentLogger().warn("You are running version {}, the latest supported version is {}", running, version);
                getComponentLogger().warn("Update at https://modrinth.com/plugin/commander-1 or https://hangar.papermc.io/TheNextLvl/CommandControl");
            } else {
                getComponentLogger().warn("You are running a snapshot version of Commander");
            }
        }));
    }

    @Override
    public void onEnable() {
        Bukkit.getGlobalRegionScheduler().execute(this, () -> {
            permissionOverride().overridePermissions();
            commandRegistry().unregisterCommands();
        });
        registerListeners();
        registerCommands();
    }

    @Override
    public void onDisable() {
        commandRegistry().getHiddenFile().save();
        commandRegistry().getUnregisteredFile().save();
        permissionOverride().getOverridesFile().save();
        metrics.shutdown();
    }

    private void registerCommands() {
        new CommanderCommand().register(this);
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new CommandListener(this), this);
    }
}
