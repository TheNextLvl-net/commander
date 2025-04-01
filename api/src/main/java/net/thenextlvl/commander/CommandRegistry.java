package net.thenextlvl.commander;

import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Set;

@NullMarked
public interface CommandRegistry {
    @Unmodifiable
    Set<String> hiddenCommands();

    @Unmodifiable
    Set<String> unregisteredCommands();

    boolean hide(String command);

    boolean isHidden(String command);

    boolean isUnregistered(String command);

    boolean register(String command);

    boolean reveal(String command);

    boolean unregister(String command);

    void unregisterCommands();
}
