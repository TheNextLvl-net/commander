package net.thenextlvl.commander;

import net.thenextlvl.commander.api.Commander;
import net.thenextlvl.commander.command.CommanderCommand;
import net.thenextlvl.commander.implementation.CraftCommander;
import net.thenextlvl.commander.listener.CommandListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class CommanderPlugin extends JavaPlugin {

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
        var commander = (CraftCommander) registration.getProvider();

        Bukkit.getScheduler().runTask(this, () -> commander.platform().permissionRegistry().overridePermissions());

        registerCommands(commander);
        registerListeners(commander);
    }

    private void registerCommands(CraftCommander commander) {
        Bukkit.getCommandMap().register(getName(), new CommanderCommand(commander, this));
    }

    private void registerListeners(CraftCommander commander) {
        Bukkit.getPluginManager().registerEvents(new CommandListener(commander), this);
    }
}
