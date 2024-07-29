package net.thenextlvl.commander.velocity.implementation;

import com.google.gson.reflect.TypeToken;
import core.file.FileIO;
import core.file.format.GsonFile;
import core.io.IO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.commander.PermissionOverride;
import net.thenextlvl.commander.velocity.CommanderPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class ProxyPermissionOverride implements PermissionOverride {
    private final FileIO<Map<String, @Nullable String>> overridesFile;

    public ProxyPermissionOverride(CommanderPlugin plugin) {
        this.overridesFile = new GsonFile<Map<String, @Nullable String>>(
                IO.of(plugin.dataFolder().toFile(), "permission-overrides.json"),
                new HashMap<>(), new TypeToken<>() {
        }).saveIfAbsent();
    }

    @Override
    public Map<String, @Nullable String> overrides() {
        return Map.copyOf(overridesFile.getRoot());
    }

    @Override
    public Map<String, @Nullable String> originalPermissions() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public @Nullable String originalPermission(String command) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isOverridden(String command) {
        return overridesFile.getRoot().containsKey(command);
    }

    @Override
    public boolean override(String command, @Nullable String permission) {
        overridesFile.getRoot().put(command, permission);
        return true;
    }

    @Override
    public boolean reset(String command) {
        if (!isOverridden(command)) return false;
        overridesFile.getRoot().remove(command);
        return true;
    }

    @Override
    public void overridePermissions() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
