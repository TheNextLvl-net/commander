package net.thenextlvl.commander.paper.implementation;

import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.thenextlvl.commander.api.platform.PermissionManager;
import org.bukkit.command.Command;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@RequiredArgsConstructor
@Accessors(fluent = true)
@TypesAreNotNullByDefault
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
public class CraftPermissionManager implements PermissionManager<Command> {
    private final CraftCommander commander;
    private final Map<String, @Nullable String> originalPermissions = new HashMap<>();

    @Override
    public boolean overridePermissions(String query, @Nullable String permission) {
        var success = new AtomicBoolean();
        commander().commandManager().getCommands(query).forEach(command -> {
            if (overridePermission(command, permission)) success.set(true);
        });
        return success.get();
    }

    @Override
    public boolean resetPermissions(String query) {
        var success = new AtomicBoolean();
        commander().commandManager().getCommands(query).forEach(command -> {
            if (resetPermission(command)) success.set(true);
        });
        return success.get();
    }

    @Override
    public boolean resetPermission(Command command) {
        if (!originalPermissions().containsKey(command.getLabel())) return false;
        var permission = originalPermissions().remove(command.getLabel());
        if (Objects.equals(command.getPermission(), permission)) return false;
        command.setPermission(permission);
        return Objects.equals(command.getPermission(), permission);
    }

    @Override
    public boolean overridePermission(Command command, @Nullable String permission) {
        originalPermissions().putIfAbsent(command.getLabel(), command.getPermission());
        if (Objects.equals(command.getPermission(), permission)) return false;
        command.setPermission(permission);
        return Objects.equals(command.getPermission(), permission);
    }

    @Override
    public @Nullable String getOriginalPermission(String literal) {
        return originalPermissions().get(literal);
    }

    @Override
    public boolean hasOriginalPermission(String literal) {
        return !originalPermissions().containsKey(literal);
    }
}
