package net.thenextlvl.commander.velocity.implementation;

import net.thenextlvl.commander.CommonPermissionOverride;
import net.thenextlvl.commander.velocity.ProxyCommander;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;

@NullMarked
public class ProxyPermissionOverride extends CommonPermissionOverride {
    public ProxyPermissionOverride(final ProxyCommander commander) {
        super(commander);
    }

    @Override
    public @Unmodifiable Map<String, @Nullable String> originalPermissions() {
        return Map.of();
    }

    @Override
    public @Nullable String originalPermission(final String command) {
        return null;
    }

    @Override
    public void overridePermissions() {
    }

    @Override
    protected boolean internalOverride(final String command, final String permission) {
        return true;
    }

    @Override
    protected boolean internalReset(final String command) {
        return true;
    }
}