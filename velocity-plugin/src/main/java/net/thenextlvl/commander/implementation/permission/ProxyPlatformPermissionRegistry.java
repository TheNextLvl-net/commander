package net.thenextlvl.commander.implementation.permission;

import net.thenextlvl.commander.api.permission.PlatformPermissionRegistry;
import net.thenextlvl.commander.implementation.ProxyCommander;
import org.jetbrains.annotations.Nullable;

public record ProxyPlatformPermissionRegistry(ProxyCommander commander) implements PlatformPermissionRegistry {

    @Override
    public boolean overridePermission(String literal, @Nullable String permission) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @Nullable String getOriginalPermission(String literal) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasOriginalPermission(String literal) {
        throw new UnsupportedOperationException();
    }
}
