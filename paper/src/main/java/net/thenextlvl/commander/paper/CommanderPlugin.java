package net.thenextlvl.commander.paper;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.key.Key;
import net.thenextlvl.commander.Commander;
import net.thenextlvl.commander.command.CommanderCommand;
import net.thenextlvl.commander.paper.listener.CommandListener;
import net.thenextlvl.commander.paper.version.CommanderVersionChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

import java.nio.file.Path;

@NullMarked
public class CommanderPlugin extends JavaPlugin {
    private final Metrics metrics = new Metrics(this, 22782);
    private final CommanderVersionChecker versionChecker = new CommanderVersionChecker(this);
    public final PaperCommander commons = new PaperCommander(this); // todo: weaken visibility

    private final Key key = Key.key("commander", "translations");
    private final Path translations = getDataPath().resolve("translations");

    public CommanderPlugin() {
        registerCommands();
    }
    
    @Override
    public void onLoad() {
        getServer().getServicesManager().register(Commander.class, commons, this, ServicePriority.Highest);
        versionChecker.checkVersion();
    }

    @Override
    public void onEnable() {
        getServer().getGlobalRegionScheduler().execute(this, () -> {
            commons.permissionOverride().overridePermissions();
            commons.commandRegistry().unregisterCommands();
        });
        registerListeners();
    }

    @Override
    public void onDisable() {
        commons.commandRegistry().save(true);
        commons.permissionOverride().save(true);
        metrics.shutdown();
    }

    private void registerCommands() {
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(event -> {
            event.registrar().register(CommanderCommand.create(commons), "The main command to interact with Commander");
        }));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new CommandListener(this), this);
    }
}
