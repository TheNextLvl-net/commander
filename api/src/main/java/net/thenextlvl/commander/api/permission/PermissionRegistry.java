package net.thenextlvl.commander.api.permission;

import com.google.gson.reflect.TypeToken;
import core.api.file.format.GsonFile;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.commander.api.Commander;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class PermissionRegistry {
    private final GsonFile<HashMap<String, String>> file;
    private final @Getter Map<String, String> originalPermissions = new HashMap<>();
    private final @Getter Commander commander;

    protected PermissionRegistry(Commander commander, File dataFolder) {
        this(new GsonFile<>(
                new File(dataFolder, "permission-override.json"),
                new HashMap<String, String>(),
                new TypeToken<>() {
                }
        ).saveIfAbsent(), commander);
    }

    /**
     * Reset the permission of a certain command
     *
     * @param literal the command literal
     * @return whether the permission was overridden in the first place
     */
    public boolean resetPermission(String literal) {
        if (!getPermissionOverride().containsKey(literal)) return false;
        getPermissionOverride().remove(literal);
        file.save();
        return true;
    }

    public void overridePermission(String literal, @Nullable String permission) {
        getPermissionOverride().put(literal, permission);
        file.save();
    }

    /**
     * Get a map of all overridden permissions<br/>
     * The key being the command pattern and the value being the new permission
     *
     * @return all overridden permissions
     */
    public Map<String, String> getPermissionOverride() {
        return file.getRoot();
    }
}
