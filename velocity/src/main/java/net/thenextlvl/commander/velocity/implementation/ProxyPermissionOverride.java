package net.thenextlvl.commander.velocity.implementation;

import net.thenextlvl.commander.CommonPermissionOverride;
import net.thenextlvl.commander.velocity.ProxyCommander;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;

@NullMarked
public class ProxyPermissionOverride extends CommonPermissionOverride {
    public ProxyPermissionOverride(ProxyCommander commander) {
        super(commander);
    }

    @Override
    public @Unmodifiable Map<String, @Nullable String> originalPermissions() {
        return Map.of();
    }

    @Override
    public @Nullable String originalPermission(String command) {
        return null;
    }

    @Override
    public void overridePermissions() {
    }

    @Override
    protected boolean internalOverride(String command, @Nullable String permission) {
        // todo: try to override the canUse method of the command
        return true;
    }

    @Override
    protected boolean internalReset(String command) {
        return true;
    }
}