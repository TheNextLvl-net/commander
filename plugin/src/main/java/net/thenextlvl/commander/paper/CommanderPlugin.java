package net.thenextlvl.commander.paper;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.thenextlvl.commander.api.Commander;
import net.thenextlvl.commander.paper.command.CommanderCommand;
import net.thenextlvl.commander.paper.implementation.CraftCommander;
import net.thenextlvl.commander.paper.listener.CommandListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@Accessors(fluent = true)
public class CommanderPlugin extends JavaPlugin {
    private final CraftCommander commander = new CraftCommander(getDataFolder());

    @Override
    public void onLoad() {
        Bukkit.getServicesManager().register(Commander.class, commander, this, ServicePriority.Normal);
    }

    @Override
    public void onEnable() {
        Bukkit.getGlobalRegionScheduler().execute(this, () -> commander.permissionManager().overridePermissions());
        registerListeners();
        registerCommands();
    }

    private void registerCommands() {
        Bukkit.getCommandMap().register(getName(), new CommanderCommand(this));
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new CommandListener(this), this);
    }
}
