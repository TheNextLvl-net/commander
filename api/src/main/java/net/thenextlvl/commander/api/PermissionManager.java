package net.thenextlvl.commander.api;

import com.google.gson.reflect.TypeToken;
import core.api.file.format.GsonFile;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class PermissionManager {
    private final GsonFile<Map<String, String>> permissionOverride;

    protected PermissionManager(File dataFolder) {
        this(new GsonFile<>(new File(dataFolder, "permission-override.json"), new HashMap<>(), new TypeToken<>() {
        }));
        if (!getPermissionOverride().getFile().isFile()) getPermissionOverride().save();
    }

    public void overridePermissions() {
        getPermissionOverride().getRoot().forEach(this::overridePermission);
    }

    public boolean overridePermission(String command, @Nullable String permission) {
        return overridePermission(command, permission, false);
    }

    public abstract boolean overridePermission(String command, @Nullable String permission, boolean alias);

    public boolean resetPermission(String label) {
        if (!getPermissionOverride().getRoot().containsKey(label)) return false;
        if (hasOriginalPermission(label)) overridePermission(label, getOriginalPermission(label));
        getPermissionOverride().getRoot().remove(label);
        getPermissionOverride().save();
        return true;
    }

    public abstract @Nullable String getOriginalPermission(String label);

    public abstract boolean hasOriginalPermission(String label);
}
