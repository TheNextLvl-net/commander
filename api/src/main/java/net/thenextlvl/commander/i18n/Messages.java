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

    public static final MessageKey<Audience> PERMISSION_RESET = new MessageKey<>("permission.reset", FORMATTER).register();
    public static final MessageKey<Audience> PERMISSION_SET = new MessageKey<>("permission.set", FORMATTER).register();
    public static final MessageKey<Audience> PERMISSION_QUERY_DEFINED = new MessageKey<>("permission.query.defined", FORMATTER).register();
    public static final MessageKey<Audience> PERMISSION_QUERY_UNDEFINED = new MessageKey<>("permission.query.undefined", FORMATTER).register();

    public static final MessageKey<Audience> COMMAND_UNREGISTERED = new MessageKey<>("command.unregistered", FORMATTER).register();
    public static final MessageKey<Audience> COMMAND_REGISTERED = new MessageKey<>("command.registered", FORMATTER).register();
    public static final MessageKey<Audience> UNKNOWN_COMMAND = new MessageKey<>("command.unknown", FORMATTER).register();
    public static final MessageKey<Audience> NO_PERMISSION = new MessageKey<>("command.permission", FORMATTER).register();

    public static final MessageKey<Audience> RESTART_REQUIRED = new MessageKey<>("restart.required", FORMATTER).register();
    public static final MessageKey<Audience> NOTHING_CHANGED = new MessageKey<>("nothing.changed", FORMATTER).register();

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
        file.setDefault(PERMISSION_RESET, "%prefix% <white>Reset the permission of <green>%command%");
        file.setDefault(PERMISSION_SET, "%prefix% <white>Set the permission of <green>%command% <white>to <green>%permission%");
        file.setDefault(PERMISSION_QUERY_DEFINED, "%prefix% <white>The permission of <green>%command% <white>is <green>%permission%");
        file.setDefault(PERMISSION_QUERY_UNDEFINED, "%prefix% <white>The permission of <green>%command% <white>is undefined");

        file.setDefault(COMMAND_UNREGISTERED, "%prefix% <white>The command <green>%command% <white>will no longer be registered");
        file.setDefault(COMMAND_REGISTERED, "%prefix% <white>The command <green>%command% <white>can be registered again");
        file.setDefault(UNKNOWN_COMMAND, "%prefix% <red>The command <dark_gray>(<dark_red>%command%<dark_gray>) <red>does not exist");
        file.setDefault(NO_PERMISSION, "%prefix% <red>You have no rights <dark_gray>(<dark_red>%permission%<dark_gray>)");

        file.setDefault(RESTART_REQUIRED, "%prefix% <white>This change requires a restart");
        file.setDefault(NOTHING_CHANGED, "%prefix% <red>Nothing could be changed");
        file.save();
    }

    private static void initGerman() {
        var file = MessageFile.getOrCreate(Locale.forLanguageTag("de-DE"));
        file.setDefault(PERMISSION_RESET, "%prefix% <white>Die Berechtigung auf <green>%command% <white>wurde zurückgesetzt");
        file.setDefault(PERMISSION_SET, "%prefix% <white>Die Berechtigung auf <green>%command% <white>wurde zu <green>%permission% <white>geändert");
        file.setDefault(PERMISSION_QUERY_DEFINED, "%prefix% <white>Die Berechtigung auf <green>%command% <white>ist <green>%permission%");
        file.setDefault(PERMISSION_QUERY_UNDEFINED, "%prefix% <white>Die Berechtigung auf <green>%command% <white>ist undefiniert");

        file.setDefault(COMMAND_UNREGISTERED, "%prefix% <white>Der Befehl <green>%command% <white>wird nicht länger registriert");
        file.setDefault(COMMAND_REGISTERED, "%prefix% <white>Der Befehl <green>%command% <white>kann wieder registriert werden");
        file.setDefault(UNKNOWN_COMMAND, "%prefix% <red>Der Befehl <dark_gray>(<dark_red>%command%<dark_gray>) <red>existiert nicht");
        file.setDefault(NO_PERMISSION, "%prefix%<red> Darauf hast du keine rechte <dark_gray>(<dark_red>%permission%<dark_gray>)");

        file.setDefault(RESTART_REQUIRED, "%prefix% <white>Diese Änderung benötigt einen Neustart");
        file.setDefault(NOTHING_CHANGED, "%prefix% <red>Es konnte nichts geändert werden");
        file.save();
    }
}
