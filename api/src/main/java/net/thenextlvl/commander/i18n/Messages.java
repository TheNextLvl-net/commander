package net.thenextlvl.commander.i18n;

import core.annotation.FieldsAreNonnullByDefault;
import core.api.file.format.MessageFile;
import core.api.placeholder.MessageKey;
import core.api.placeholder.SystemMessageKey;
import net.kyori.adventure.audience.Audience;

import java.util.Locale;

import static net.thenextlvl.commander.i18n.Placeholders.FORMATTER;

@FieldsAreNonnullByDefault
public class Messages {
    public static final Locale ENGLISH = Locale.forLanguageTag("en-US");

    public static final SystemMessageKey<Audience> PREFIX = new SystemMessageKey<>("commander.prefix", FORMATTER).register();

    public static final MessageKey<Audience> UNKNOWN_COMMAND = new MessageKey<>("command.unknown", FORMATTER).register();
    public static final MessageKey<Audience> NO_PERMISSION = new MessageKey<>("command.permission", FORMATTER).register();

    static {
        initRoot();
        initEnglish();
        initGerman();
    }

    private static void initRoot() {
        var file = MessageFile.ROOT;
        file.setDefault(PREFIX, "<white>Commander <dark_gray>»<reset>");
        file.save();
    }

    private static void initEnglish() {
        var file = MessageFile.getOrCreate(ENGLISH);
        file.setDefault(UNKNOWN_COMMAND, "%prefix% <red>The command <dark_gray>(<dark_red>%command%<dark_gray>) <red>does not exist");
        file.setDefault(NO_PERMISSION, "%prefix% <red>You have no rights <dark_gray>(<dark_red>%permission%<dark_gray>)");
        file.save();
    }

    private static void initGerman() {
        var file = MessageFile.getOrCreate(Locale.forLanguageTag("de-DE"));
        file.setDefault(UNKNOWN_COMMAND, "%prefix% <red>Der Befehl <dark_gray>(<dark_red>%command%<dark_gray>) <red>existiert nicht");
        file.setDefault(NO_PERMISSION, "%prefix%<red> Darauf hast du keine rechte <dark_gray>(<dark_red>%permission%<dark_gray>)");
        file.save();
    }
}
