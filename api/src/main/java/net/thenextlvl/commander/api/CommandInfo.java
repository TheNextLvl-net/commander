package net.thenextlvl.commander.api;

import com.google.gson.annotations.SerializedName;
import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import lombok.*;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

@Getter
@Setter
@EqualsAndHashCode
@Accessors(fluent = true)
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandInfo {
    private final @SerializedName("command") String query;
    private @Nullable String permission;
    private @Nullable Status status;

    /**
     * Create a new {@link CommandInfo} instance with status {@link Status#REMOVED removed}
     *
     * @param query the command query
     * @return the command info
     */
    public static CommandInfo remove(String query) {
        return new CommandInfo(query, null, Status.REMOVED);
    }

    /**
     * Create a new {@link CommandInfo} instance with status {@link Status#HIDDEN hidden}
     *
     * @param query the command query
     * @return the command info
     */
    public static CommandInfo hide(String query) {
        return new CommandInfo(query, null, Status.HIDDEN);
    }

    /**
     * Create a new {@link CommandInfo} instance overriding a permission
     *
     * @param query the command query
     * @return the command info
     */
    public static CommandInfo override(String query, @Nullable String permission) {
        return new CommandInfo(query, permission, null);
    }

    /**
     * Test whether the command matches the given query
     *
     * @param command the command
     * @param query   the command query
     * @return whether the command matches the given query
     */
    public static boolean nameMatches(String command, String query) {
        return command.equals(query)
                || (query.contains("*") && compile(query).matcher(command).matches())
                || (query.startsWith("..") && command.endsWith(query.substring(2)))
                || (query.endsWith("..") && command.startsWith(query.substring(0, query.length() - 2)));
    }

    /**
     * Compile a command query
     *
     * @param query the command query
     * @return the pattern
     */
    public static Pattern compile(String query) {
        return Pattern.compile(query.replaceAll("\\*", ".+"));
    }

    /**
     * @see CommandInfo#nameMatches(String, String)
     */
    public boolean nameMatches(String command) {
        return CommandInfo.nameMatches(command, query());
    }

    /**
     * Get whether the command is hidden
     *
     * @return whether the command is {@link Status#HIDDEN hidden}
     */
    public boolean isHidden() {
        return Status.HIDDEN.equals(status());
    }

    /**
     * Get whether the command is removed
     *
     * @return whether the command is {@link Status#REMOVED removed}
     */
    public boolean isRemoved() {
        return Status.REMOVED.equals(status());
    }

    public enum Status {
        /**
         * Indicates whether a command should not be propagated
         */
        @SerializedName("hidden") HIDDEN,
        /**
         * Indicates whether a command should be removed
         */
        @SerializedName("removed") REMOVED
    }
}
