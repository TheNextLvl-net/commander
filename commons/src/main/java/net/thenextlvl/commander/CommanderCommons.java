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
public abstract class CommanderCommons implements Commander {
    private final Key key = Key.key("commander", "translations");
    private final Path translations = getDataPath().resolve("translations");
    private final ComponentBundle bundle = ComponentBundle.builder(key, translations)
            .placeholder("prefix", "prefix")
            .miniMessage(MiniMessage.builder().tags(TagResolver.resolver(
                    TagResolver.standard(),
                    Placeholder.parsed("root_command", getRootCommand())
            )).build())
            .resource("commander.properties", Locale.US)
            .resource("commander_german.properties", Locale.GERMANY)
            .build();

    @Override
    public abstract CommonCommandRegistry commandRegistry();

    @Override
    public abstract CommonPermissionOverride permissionOverride();

    public ComponentBundle bundle() {
        return bundle;
    }

    public abstract Logger logger();

    public abstract <S> BrigadierAccess<S> brigadierAccess();

    public abstract String getRootCommand();

    public abstract Path getDataPath();

    public abstract Stream<String> getKnownCommands();

    public abstract Stream<String> getKnownPermissions();

    public abstract void updateCommands();

    public abstract void conflictSave(Audience audience);

    public abstract void hiddenConflictSave(Audience audience);

    public abstract void unregisteredConflictSave(Audience audience);

    public abstract void permissionConflictSave(Audience audience);
}
