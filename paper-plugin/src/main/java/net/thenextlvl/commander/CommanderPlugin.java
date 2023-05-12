package net.thenextlvl.commander;

import core.bukkit.plugin.CorePlugin;
import net.thenextlvl.commander.api.Commander;
import net.thenextlvl.commander.command.CommanderCommand;
import net.thenextlvl.commander.implementation.CraftCommander;
import net.thenextlvl.commander.listener.CommandListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

public class CommanderPlugin extends CorePlugin {

    @Override
    public void onLoad() {
        Bukkit.getServicesManager().register(
                Commander.class,
                new CraftCommander(getDataFolder()),
                this,
                ServicePriority.Normal
        );
    }

    @Override
    public void onEnable() {
        var registration = Bukkit.getServicesManager().getRegistration(Commander.class);
        if (registration == null) return;
        var commander = registration.getProvider();

        Bukkit.getScheduler().runTask(this, () -> {
            commander.commandManager().unregisterCommands();
            commander.permissionManager().overridePermissions();
        });

        registerCommands(commander);
        registerListeners(commander);
    }

    private void registerCommands(Commander commander) {
        registerCommand("command", new CommanderCommand(commander));
    }

    private void registerListeners(Commander commander) {
        Bukkit.getPluginManager().registerEvents(new CommandListener(commander), this);
    }
}
