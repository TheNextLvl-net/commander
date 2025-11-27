package net.thenextlvl.commander.access;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.kyori.adventure.audience.Audience;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class BrigadierAccess<S> {
    public abstract LiteralArgumentBuilder<S> literal(String name);

    public abstract <T> RequiredArgumentBuilder<S, T> argument(String name, ArgumentType<T> type);

    public abstract boolean hasPermission(S source, String permission);

    public abstract Audience audience(S source);
}
