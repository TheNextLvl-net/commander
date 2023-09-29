package net.thenextlvl.commander.implementation;

import core.i18n.file.ComponentBundle;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.commander.api.CommandRegistry;
import net.thenextlvl.commander.api.Commander;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Locale;

@Getter
@Accessors(fluent = true)
public class CraftCommander implements Commander<Command> {
    private final ComponentBundle bundle;
    private final CommandRegistry commandRegistry;
    private final CraftCommandManager commandManager;
    private final CraftPermissionManager permissionManager;

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
        commandRegistry = new CommandRegistry(this, dataFolder);
        commandManager = new CraftCommandManager(this);
        permissionManager = new CraftPermissionManager(this);
    }
}
