package net.thenextlvl.commander.paper;

import core.i18n.file.ComponentBundle;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.commander.CommandFinder;
import net.thenextlvl.commander.Commander;
import net.thenextlvl.commander.paper.command.CommanderCommand;
import net.thenextlvl.commander.paper.implementation.PaperCommandFinder;
import net.thenextlvl.commander.paper.implementation.PaperCommandRegistry;
import net.thenextlvl.commander.paper.implementation.PaperPermissionOverride;
import net.thenextlvl.commander.paper.listener.CommandListener;
import net.thenextlvl.commander.paper.version.CommanderVersionChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

import java.nio.file.Path;
import java.util.Locale;

@NullMarked
public class CommanderPlugin extends JavaPlugin implements Commander {
    public static final String ROOT_COMMAND = "command";
    private final Metrics metrics = new Metrics(this, 22782);
    private final CommanderVersionChecker versionChecker = new CommanderVersionChecker(this);

    private final Key key = Key.key("commander", "translations");
    private final Path translations = getDataPath().resolve("translations");
    private final ComponentBundle bundle = ComponentBundle.builder(key, translations)
            .placeholder("prefix", "prefix")
            .miniMessage(MiniMessage.builder().tags(TagResolver.resolver(
                    TagResolver.standard(),
                    Placeholder.parsed("root_command", ROOT_COMMAND)
            )).build())
            .resource("commander.properties", Locale.US)
            .resource("commander_german.properties", Locale.GERMANY)
            .build();

    private final PaperCommandFinder commandFinder = new PaperCommandFinder(this);
    private final PaperCommandRegistry commandRegistry = new PaperCommandRegistry(this);
    private final PaperPermissionOverride permissionOverride = new PaperPermissionOverride(this);

    @Override
    public void onLoad() {
        getServer().getServicesManager().register(Commander.class, this, this, ServicePriority.Highest);
        versionChecker.checkVersion();
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
        commandRegistry.save(true);
        permissionOverride.save(true);
        metrics.shutdown();
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
    public PaperCommandRegistry commandRegistry() {
        return commandRegistry;
    }

    @Override
    public PaperPermissionOverride permissionOverride() {
        return permissionOverride;
    }

    public void autoSave(Audience audience) {
        if (commandRegistry.save(false) & permissionOverride.save(false)) return;
        bundle().sendMessage(audience, "command.save.conflict");
    }

    private void registerCommands() {
        CommanderCommand.register(this);
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new CommandListener(this), this);
    }
}
