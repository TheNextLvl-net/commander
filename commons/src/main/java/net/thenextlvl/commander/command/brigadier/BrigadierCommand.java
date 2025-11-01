package net.thenextlvl.commander.command.brigadier;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.thenextlvl.commander.CommanderCommons;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@NullMarked
public abstract class BrigadierCommand<S> {
    protected final CommanderCommons commons;

    private final @Nullable String permission;
    private final String name;

    protected BrigadierCommand(CommanderCommons commons, String name, @Nullable String permission) {
        this.commons = commons;
        this.permission = permission;
        this.name = name;
    }

    protected LiteralArgumentBuilder<S> create() {
        return commons.<S>brigadierAccess().literal(name)
                .requires(this::canUse);
    }

    protected boolean canUse(S source) {
        return permission == null || commons.brigadierAccess().hasPermission(source, permission);
    }

    protected <T> Optional<T> tryGetArgument(CommandContext<S> context, String name, Class<T> type) {
        try {
            return Optional.of(context.getArgument(name, type));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("No such argument '" + name + "' exists on this command"))
                return Optional.empty();
            throw e;
        }
    }
}
