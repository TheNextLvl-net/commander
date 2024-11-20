package net.thenextlvl.commander.paper;

import core.i18n.file.ComponentBundle;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.commander.Commander;
import net.thenextlvl.commander.paper.command.CommanderCommand;
import net.thenextlvl.commander.paper.implementation.PaperCommandFinder;
import net.thenextlvl.commander.paper.implementation.PaperCommandRegistry;
import net.thenextlvl.commander.paper.implementation.PaperPermissionOverride;
import net.thenextlvl.commander.paper.listener.CommandListener;
import net.thenextlvl.commander.paper.version.CommanderVersionChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.util.Locale;

@Getter
@NullMarked
@Accessors(fluent = true)
public class CommanderPlugin extends JavaPlugin implements Commander {
    private final Metrics metrics = new Metrics(this, 22782);
    private final CommanderVersionChecker versionChecker = new CommanderVersionChecker(this);

    private final File translations = new File(getDataFolder(), "translations");
    private final ComponentBundle bundle = new ComponentBundle(translations, audience ->
            audience instanceof Player player ? player.locale() : Locale.US)
            .register("commander", Locale.US)
            .register("commander_german", Locale.GERMANY)
            .miniMessage(bundle -> MiniMessage.builder().tags(TagResolver.resolver(
                    TagResolver.standard(),
                    Placeholder.component("prefix", bundle.component(Locale.US, "prefix"))
            )).build());

    private final PaperCommandFinder commandFinder = new PaperCommandFinder(this);
    private final PaperCommandRegistry commandRegistry = new PaperCommandRegistry(this);
    private final PaperPermissionOverride permissionOverride = new PaperPermissionOverride(this);

    @Override
    public void onLoad() {
        getServer().getServicesManager().register(Commander.class, this, this, ServicePriority.Highest);
        versionChecker().checkVersion();
    }

    @Override
    public void onEnable() {
        getServer().getGlobalRegionScheduler().execute(this, () -> {
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
        metrics().shutdown();
    }

    private void registerCommands() {
        new CommanderCommand().register(this);
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new CommandListener(this), this);
    }
}
