package net.thenextlvl.commander.api;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import core.annotation.FieldsAreNotNullByDefault;
import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import core.api.file.format.GsonFile;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

@TypesAreNotNullByDefault
@FieldsAreNotNullByDefault
@ParametersAreNotNullByDefault
@MethodsReturnNotNullByDefault
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandRegistry {
    private final GsonFile<HashSet<CommandInfo>> commandsFile;
    private final @Getter Commander commander;

    /**
     * Construct a command information registry
     *
     * @param commander  the corresponding command instance
     * @param dataFolder the data-folder to create the files in
     */
    public CommandRegistry(Commander commander, File dataFolder) {
        this(new GsonFile<>(
                new File(dataFolder, "commands.json"),
                new HashSet<CommandInfo>(),
                new TypeToken<>() {
                },
                new GsonBuilder()
                        .setPrettyPrinting()
                        .create()
        ).saveIfAbsent(), commander);
    }

    /**
     * Register a {@link CommandInfo} to the {@link CommandRegistry#commandsFile}
     *
     * @param info the info to register
     * @return whether the command info could be registered
     */
    public boolean registerCommandInfo(CommandInfo info) {
        var added = getCommandInformation().add(info);
        if (added) commandsFile.save();
        return added;
    }

    /**
     * Remove a {@link CommandInfo} from the {@link CommandRegistry#commandsFile}
     *
     * @param filter what to remove
     * @return whether the info could be removed
     */
    public boolean removeCommandInfo(Predicate<CommandInfo> filter) {
        var removed = getCommandInformation().removeIf(filter);
        if (removed) commandsFile.save();
        return removed;
    }

    /**
     * Reset the permissions of every command matching the given query
     *
     * @param query the command query
     * @return whether any permission could be reset
     */
    public boolean resetPermissions(String query) {
        var commands = getCommandInformation().stream()
                .filter(info -> info.permission() != null
                        && info.status() != null
                        && info.nameMatches(query))
                .toList();
        commands.forEach(info -> info.permission(null));
        var removed = getCommandInformation().removeIf(info -> info.nameMatches(query)
                && info.permission() != null && info.status() == null)
                || !commands.isEmpty();
        if (!removed) return false;
        commander.permissionManager().resetPermissions(query);
        commandsFile.save();
        return true;
    }

    /**
     * Override the permissions of every command matching the given query
     *
     * @param query      the command query
     * @param permission the new permission
     * @return whether any permission could be overridden
     */
    public boolean overridePermissions(String query, @Nullable String permission) {
        if (!registerCommandInfo(CommandInfo.override(query, permission))) return false;
        return commander.permissionManager().overridePermissions(query, permission);
    }

    /**
     * Register a command again
     *
     * @param literal the command literal
     * @return whether the command could be registered again
     */
    public boolean registerCommand(String literal) {
        var commands = getCommandInformation().stream()
                .filter(info -> info.query().equals(literal)
                        && info.permission() != null
                        && info.isRemoved())
                .toList();
        commands.forEach(info -> info.status(null));
        var removed = getCommandInformation().removeIf(info -> info.query().equals(literal)
                && info.permission() == null && info.isRemoved())
                || !commands.isEmpty();
        if (removed) commandsFile.save();
        return removed;
    }

    /**
     * Reveal a command again
     *
     * @param literal the command literal
     * @return whether the command could be revealed again
     */
    public boolean revealCommand(String literal) {
        var commands = getCommandInformation().stream()
                .filter(info -> info.query().equals(literal)
                        && info.permission() != null
                        && info.isHidden())
                .toList();
        commands.forEach(info -> info.status(null));
        var removed = getCommandInformation().removeIf(info -> info.query().equals(literal)
                && info.permission() == null && info.isHidden())
                || !commands.isEmpty();
        if (removed) commandsFile.save();
        return removed;
    }

    /**
     * Get whether a command query is removed
     *
     * @param filter what to look for
     * @return whether a command info is registered
     */
    public boolean containsCommandInfo(Predicate<CommandInfo> filter) {
        return getCommandInformation().stream().anyMatch(filter);
    }

    public boolean isRemoved(String literal) {
        return containsCommandInfo(info -> info.isRemoved() && info.nameMatches(literal));
    }

    public boolean isHidden(String literal) {
        return containsCommandInfo(info -> info.isHidden() && info.nameMatches(literal));
    }

    /**
     * Get a certain command information
     *
     * @return the command information
     */
    public Optional<CommandInfo> getCommandInformation(String literal) {
        return getCommandInformation().stream()
                .filter(info -> info.nameMatches(literal))
                .findFirst();
    }

    /**
     * Get all command information
     *
     * @return all command information
     */
    public Set<CommandInfo> getCommandInformation() {
        return commandsFile.getRoot();
    }
}
