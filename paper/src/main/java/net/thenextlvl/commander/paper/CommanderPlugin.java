package net.thenextlvl.commander.paper;

import com.mojang.brigadier.CommandDispatcher;
import dev.faststats.bukkit.BukkitMetrics;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.thenextlvl.commander.command.CommanderCommand;
import net.thenextlvl.commander.paper.listener.CommandListener;
import net.thenextlvl.commander.paper.version.CommanderVersionChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;

@NullMarked
public class CommanderPlugin extends JavaPlugin {
    private final Metrics metrics = new Metrics(this, 22782);
    private final dev.faststats.core.Metrics fastStats = BukkitMetrics.factory()
            .token("417c37aa7e3b468fc09ee54af4336490")
            .create(this);
    private final CommanderVersionChecker versionChecker = new CommanderVersionChecker(this);
    private final PaperCommander commons = new PaperCommander(this);

    private @Nullable CommandDispatcher<CommandSourceStack> commandDispatcher = null;

    public CommanderPlugin() throws IOException {
        registerCommands();
    }

    @Override
    public void onLoad() {
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

    public @Nullable CommandDispatcher<CommandSourceStack> commandDispatcher() {
        return commandDispatcher;
    }

    private void registerCommands() {
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(event -> {
            event.registrar().register(CommanderCommand.create(commons), "The main command to interact with Commander");
            commandDispatcher = event.registrar().getDispatcher();
        }));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new CommandListener(), this);
    }
}
