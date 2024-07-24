package net.thenextlvl.commander.api;

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
}
