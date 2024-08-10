package net.thenextlvl.commander;

import net.kyori.adventure.audience.Audience;

import java.util.Set;

public interface CommandRegistry {
    Set<String> hiddenCommands();

    Set<String> unregisteredCommands();

    boolean hide(String command);

    boolean isHidden(String command);

    boolean isUnregistered(String command);

    boolean register(String command);

    boolean reveal(String command);

    boolean unregister(String command);

    void unregisterCommands();

    boolean reload(Audience audience);
}
