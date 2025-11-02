package net.thenextlvl.commander.paper.implementation;

import com.mojang.brigadier.tree.CommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.commander.CommonPermissionOverride;
import net.thenextlvl.commander.paper.PaperCommander;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

@NullMarked
public class PaperPermissionOverride extends CommonPermissionOverride {
    private final Map<String, @Nullable String> originalPermissions = new HashMap<>();
    private final Map<String, Predicate<CommandSourceStack>> originalBrigadierPermissions = new HashMap<>();

    public PaperPermissionOverride(PaperCommander commander) {
        super(commander);
    }

    @Override
    public @Unmodifiable Map<String, @Nullable String> originalPermissions() {
        return Map.copyOf(originalPermissions);
    }

    @Override
    public @Nullable String originalPermission(String command) {
        return originalPermissions.get(command);
    }

    @Override
    public void overridePermissions() {
        overridesFile.getRoot().forEach(this::internalOverride);
    }

    @Override
    protected boolean internalOverride(String command, @Nullable String permission) {
        var plugin = ((PaperCommander) commons).getPlugin();
        var registered = plugin.getServer().getCommandMap().getKnownCommands().get(command);

        var registeredPermission = registered != null ? registered.getPermission() : null;
        if (Objects.equals(registeredPermission, permission)) {
            System.out.println("#1");
            return false;
        }
        originalPermissions.putIfAbsent(command, registeredPermission);

        var dispatcher = plugin.commandDispatcher();
        var child = dispatcher != null ? dispatcher.getRoot().getChild(command) : null;
        if (child != null) {
            // fixme: still not working
            child.getChildren().forEach(node -> {
                var original = originalBrigadierPermissions.putIfAbsent(command, node.getRequirement());
                var requirement = original != null ? original : node.getRequirement();
                setField(CommandNode.class, node, "requirement", permission != null
                        ? requirement.and(source -> {
                    // fixme: still not being checked
                    System.out.println("Checking permission for " + command + ": " + permission);
                    return source.getSender().hasPermission(permission);
                }) : ((Predicate<CommandSourceStack>) source -> true));
                System.out.println("set brigadier requirement for " + command + " to " + permission + " for node " + node.getName());
            });
            return true;
        }

        if (registered == null) {
            System.out.println("#2");
            return false;
        }
        System.out.println("set permission for " + command + " to " + permission);
        registered.setPermission(permission);
        return Objects.equals(registered.getPermission(), permission);
    }

    @Override
    protected boolean internalReset(String command) {
        var plugin = ((PaperCommander) commons).getPlugin();
        var registered = plugin.getServer().getCommandMap().getKnownCommands().get(command);
        var requirement = originalBrigadierPermissions.remove(command);

        if (!originalPermissions.containsKey(command) && requirement == null) return false;
        var permission = originalPermissions.remove(command);

        var dispatcher = plugin.commandDispatcher();
        var child = dispatcher != null ? dispatcher.getRoot().getChild(command) : null;
        if (child != null && requirement != null) {
            setField(CommandNode.class, child, "requirement", requirement);
            return true;
        }

        if (registered == null) return false;
        if (Objects.equals(registered.getPermission(), permission)) return false;
        registered.setPermission(permission);
        return Objects.equals(registered.getPermission(), permission);
    }

    private void setField(Class<?> clazz, Object object, String fieldName, Object value) {
        try {
            var declaredField = clazz.getDeclaredField(fieldName);
            var access = declaredField.canAccess(object);
            if (!access) declaredField.setAccessible(true);
            declaredField.set(object, value);
            if (!access) declaredField.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
