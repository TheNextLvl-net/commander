package net.thenextlvl.commander.i18n;

import core.api.file.format.MessageFile;
import core.api.placeholder.MessageKey;
import core.api.placeholder.Placeholder;
import core.api.placeholder.SystemMessageKey;
import net.kyori.adventure.audience.Audience;

import java.util.Locale;

@SuppressWarnings({"UnstableApiUsage", "removal", "deprecation"})
public class Messages {
    public static final Placeholder.Formatter<Audience> FORMATTER = new Placeholder.Formatter<>();
    public static final SystemMessageKey<Audience> PREFIX = new SystemMessageKey<>("commander.prefix", FORMATTER).register();

    public static final MessageKey<Audience> PERMISSION_RESET = new MessageKey<>("permission.reset", FORMATTER).register();
    public static final MessageKey<Audience> PERMISSION_SET = new MessageKey<>("permission.set", FORMATTER).register();
    public static final MessageKey<Audience> PERMISSION_QUERY_DEFINED = new MessageKey<>("permission.query.defined", FORMATTER).register();
    public static final MessageKey<Audience> PERMISSION_QUERY_UNDEFINED = new MessageKey<>("permission.query.undefined", FORMATTER).register();

    public static final MessageKey<Audience> COMMAND_UNREGISTERED = new MessageKey<>("command.unregistered", FORMATTER).register();
    public static final MessageKey<Audience> COMMAND_REGISTERED = new MessageKey<>("command.registered", FORMATTER).register();
    public static final MessageKey<Audience> UNKNOWN_COMMAND = new MessageKey<>("command.unknown", FORMATTER).register();
    public static final MessageKey<Audience> NO_PERMISSION = new MessageKey<>("command.permission", FORMATTER).register();

    public static final MessageKey<Audience> NOTHING_CHANGED = new MessageKey<>("nothing.changed", FORMATTER).register();

    static {
        initRoot();
        initEnglish();
        initGerman();
    }

    static {
        FORMATTER.registry().register(Placeholder.of("prefix", PREFIX.message()));
    }

    private static void initRoot() {
        var file = MessageFile.ROOT;
        file.setDefault(PREFIX, "<white>Commander <dark_gray>»<reset>");
        file.save();
    }

    private static void initEnglish() {
        var file = MessageFile.getOrCreate(Locale.US);
        file.setDefault(PERMISSION_RESET, "%prefix% <white>Reset the permission of <green>%command%");
        file.setDefault(PERMISSION_SET, "%prefix% <white>Set the permission of <green>%command% <white>to <green>%permission%");
        file.setDefault(PERMISSION_QUERY_DEFINED, "%prefix% <white>The permission of <green>%command% <white>is <green>%permission%");
        file.setDefault(PERMISSION_QUERY_UNDEFINED, "%prefix% <white>The permission of <green>%command% <white>is undefined");

        file.setDefault(COMMAND_UNREGISTERED, "%prefix% <white>All commands matching <green>%command% <white>are removed now");
        file.setDefault(COMMAND_REGISTERED, "%prefix% <white>All commands matching <green>%command% <white>can be used again");
        file.setDefault(UNKNOWN_COMMAND, "%prefix% <red>The command <dark_gray>(<dark_red>%command%<dark_gray>) <red>does not exist");
        file.setDefault(NO_PERMISSION, "%prefix% <red>You have no rights <dark_gray>(<dark_red>%permission%<dark_gray>)");

        file.setDefault(NOTHING_CHANGED, "%prefix% <red>Nothing could be changed");
        file.save();
    }

    private static void initGerman() {
        var file = MessageFile.getOrCreate(Locale.GERMANY);
        file.setDefault(PERMISSION_RESET, "%prefix% <white>Die Berechtigung auf <green>%command% <white>wurde zurückgesetzt");
        file.setDefault(PERMISSION_SET, "%prefix% <white>Die Berechtigung auf <green>%command% <white>wurde zu <green>%permission% <white>geändert");
        file.setDefault(PERMISSION_QUERY_DEFINED, "%prefix% <white>Die Berechtigung auf <green>%command% <white>ist <green>%permission%");
        file.setDefault(PERMISSION_QUERY_UNDEFINED, "%prefix% <white>Die Berechtigung auf <green>%command% <white>ist undefiniert");

        file.setDefault(COMMAND_UNREGISTERED, "%prefix% <white>Alle Befehle die <green>%command% <white>gleichen, wurden entfernt");
        file.setDefault(COMMAND_REGISTERED, "%prefix% <white>Alle Befehle die <green>%command% <white>gleichen, können wieder genutzt werden");
        file.setDefault(UNKNOWN_COMMAND, "%prefix% <red>Der Befehl <dark_gray>(<dark_red>%command%<dark_gray>) <red>existiert nicht");
        file.setDefault(NO_PERMISSION, "%prefix%<red> Darauf hast du keine rechte <dark_gray>(<dark_red>%permission%<dark_gray>)");

        file.setDefault(NOTHING_CHANGED, "%prefix% <red>Es konnte nichts geändert werden");
        file.save();
    }
}
