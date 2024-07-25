package net.thenextlvl.commander;

import core.i18n.file.ComponentBundle;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.commander.api.CommandRegistry;
import net.thenextlvl.commander.api.Commander;
import net.thenextlvl.commander.command.CommanderCommand;
import net.thenextlvl.commander.implementation.PaperCommandRegistry;
import net.thenextlvl.commander.implementation.PaperPermissionOverride;
import net.thenextlvl.commander.listener.CommandListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Locale;

@Getter
@Accessors(fluent = true)
public class CommanderPlugin extends JavaPlugin implements Commander {
    private final File translations = new File(getDataFolder(), "translations");
    private final ComponentBundle bundle = new ComponentBundle(translations, audience ->
            audience instanceof Player player ? player.locale() : Locale.US)
            .register("commander", Locale.US)
            .register("commander_german", Locale.GERMANY)
            .miniMessage(bundle -> MiniMessage.builder().tags(TagResolver.resolver(
                    TagResolver.standard(),
                    Placeholder.component("prefix", bundle.component(Locale.US, "prefix"))
            )).build());

    private final CommandRegistry commandRegistry = new PaperCommandRegistry();
    private final PaperCommandRegistry commandManager = new PaperCommandRegistry();
    private final PaperPermissionOverride permissionOverride = new PaperPermissionOverride();

    @Override
    public void onLoad() {
        Bukkit.getServicesManager().register(Commander.class, this, this, ServicePriority.Highest);
    }

    @Override
    public void onEnable() {
        Bukkit.getGlobalRegionScheduler().execute(this, () -> permissionOverride().overridePermissions());
        registerListeners();
        registerCommands();
    }

    private void registerCommands() {
        new CommanderCommand().register(this);
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new CommandListener(this), this);
    }
}
