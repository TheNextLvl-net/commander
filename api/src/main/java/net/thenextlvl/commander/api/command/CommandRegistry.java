package net.thenextlvl.commander.api.command;

import com.google.gson.reflect.TypeToken;
import core.api.file.format.GsonFile;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.commander.api.Commander;
import org.intellij.lang.annotations.RegExp;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

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
     * Registered all removed commands
     */
    public void registerCommands() {
        getCommander().commandRegistry().getRemovedCommands().forEach(this::registerCommands);
    }

    /**
     * Register removed commands based on a query pattern
     *
     * @param pattern the pattern to apply
     */
    public void registerCommands(@RegExp String pattern) {
        getCommander().commandRegistry().getRemovedCommands(pattern).forEach(this::registerCommand);
    }

    /**
     * Register a removed command again
     *
     * @param literal the command literal
     * @return whether the command was removed before
     */
    public boolean registerCommand(String literal) {
        var remove = getRemovedCommands().remove(literal);
        if (remove) removedCommandsFile.save();
        return remove;
    }

    /**
     * Unregister commands based on a pattern
     *
     * @param pattern the command pattern
     * @return whether the pattern was not registered before
     */
    public boolean unregisterCommands(@RegExp String pattern) {
        var added = getRemovedCommands().add(pattern);
        if (added) removedCommandsFile.save();
        return added;
    }

    /**
     * Get whether a literal command is removed
     *
     * @param literal the command literal
     * @return whether a command is removed
     */
    public boolean isCommandRemoved(String literal) {
        return getRemovedCommands().stream().anyMatch(literal::matches);
    }

    /**
     * Get all removed command patterns
     *
     * @return all removed command patterns
     */
    public Set<String> getRemovedCommands() {
        return removedCommandsFile.getRoot();
    }

    /**
     * Get all removed command patterns based on a query pattern
     *
     * @param pattern the pattern for filtering the removed commands
     * @return all removed commands patterns that match the query pattern
     */
    public Stream<String> getRemovedCommands(@RegExp String pattern) {
        return getRemovedCommands().stream()
                .filter(string -> string.matches(pattern));
    }
}
