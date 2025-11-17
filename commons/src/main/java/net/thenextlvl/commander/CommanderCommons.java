package net.thenextlvl.commander;

import core.i18n.file.ComponentBundle;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.commander.access.BrigadierAccess;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.Locale;
import java.util.stream.Stream;

@NullMarked
public abstract class CommanderCommons {
    private final Path dataPath;
    private final ComponentBundle bundle;

    protected CommanderCommons(Path dataPath) {
        this.dataPath = dataPath;
        var key = Key.key("commander", "translations");
        var translations = dataPath.resolve("translations");
        this.bundle = ComponentBundle.builder(key, translations)
                .placeholder("prefix", "prefix")
                .miniMessage(MiniMessage.builder().tags(TagResolver.resolver(
                        TagResolver.standard(),
                        Placeholder.parsed("root_command", getRootCommand())
                )).build())
                .resource("commander.properties", Locale.US)
                .resource("commander_german.properties", Locale.GERMANY)
                .build();
    }

    public ComponentBundle bundle() {
        return bundle;
    }

    public Path getDataPath() {
        return dataPath;
    }

    public abstract CommandFinder commandFinder();

    public abstract CommonCommandRegistry commandRegistry();

    public abstract CommonPermissionOverride permissionOverride();

    public abstract Logger logger();

    public abstract <S> BrigadierAccess<S> brigadierAccess();

    public abstract String getRootCommand();

    public abstract Stream<String> getKnownCommands();

    public abstract Stream<String> getKnownPermissions();

    public abstract void updateCommands();

    public final void conflictSave(Audience audience) {
        if (commandRegistry().save(false) & permissionOverride().save(false)) return;
        bundle().sendMessage(audience, "command.save.conflict");
    }

    public final void hiddenConflictSave(Audience audience) {
        if (commandRegistry().saveHidden(false)) return;
        bundle().sendMessage(audience, "command.save.conflict");
    }

    public final void unregisteredConflictSave(Audience audience) {
        if (commandRegistry().saveUnregistered(false)) return;
        bundle().sendMessage(audience, "command.save.conflict");
    }

    public final void permissionConflictSave(Audience audience) {
        if (permissionOverride().save(false)) return;
        bundle().sendMessage(audience, "command.save.conflict");
    }
}
