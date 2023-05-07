package net.thenextlvl.commander.i18n;

import core.annotation.FieldsAreNonnullByDefault;
import core.api.placeholder.Placeholder;
import net.kyori.adventure.audience.Audience;

@FieldsAreNonnullByDefault
public class Placeholders {
    public static final Placeholder.Formatter<Audience> FORMATTER = new Placeholder.Formatter<>();

    public static void init() {
        FORMATTER.registry().register(Placeholder.of("prefix", Messages.PREFIX.message()));
    }
}
