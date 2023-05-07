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

    public void overridePermission(String command, @Nullable String permission) {
        overridePermission(command, permission, false);
    }

    public abstract void overridePermission(String command, @Nullable String permission, boolean alias);
}
