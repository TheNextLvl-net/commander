package net.thenextlvl.commander.api.command;

import com.google.gson.reflect.TypeToken;
import core.api.file.format.GsonFile;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.commander.api.Commander;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandRegistry {
    private final GsonFile<HashSet<String>> removedCommandsFile;
    private final @Getter Commander commander;

    protected CommandRegistry(Commander commander, File dataFolder) {
        this(new GsonFile<>(
                new File(dataFolder, "removed-commands.json"),
                new HashSet<String>(),
                new TypeToken<>() {
                }
        ).saveIfAbsent(), commander);
    }

    /**
     * Register a removed command query again
     *
     * @param query the command query
     * @return whether the command query was registered
     */
    public boolean registerCommand(String query) {
        var remove = getRemovedCommands().remove(query);
        if (remove) removedCommandsFile.save();
        return remove;
    }

    /**
     * Unregister a command query
     *
     * @param query the command query
     * @return whether the pattern was not registered before
     */
    public boolean unregisterCommands(String query) {
        var added = getRemovedCommands().add(query);
        if (added) removedCommandsFile.save();
        return added;
    }

    /**
     * Get whether a command query is removed
     *
     * @param query the command query
     * @return whether a command is removed
     */
    public boolean isCommandRemoved(String query) {
        return getRemovedCommands().stream().anyMatch(s -> query.equals(s)
                || (s.contains("*") && query.matches(s.replaceAll("\\*", ".+"))));
    }

    /**
     * Get all removed command patterns
     *
     * @return all removed command patterns
     */
    public Set<String> getRemovedCommands() {
        return removedCommandsFile.getRoot();
    }
}
