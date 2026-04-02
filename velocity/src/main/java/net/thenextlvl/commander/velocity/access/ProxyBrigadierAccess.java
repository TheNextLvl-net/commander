package net.thenextlvl.commander.velocity.access;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.audience.Audience;
import net.thenextlvl.commander.access.BrigadierAccess;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ProxyBrigadierAccess extends BrigadierAccess<CommandSource> {
    @Override
    public LiteralArgumentBuilder<CommandSource> literal(final String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    @Override
    public <T> RequiredArgumentBuilder<CommandSource, T> argument(final String name, final ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    @Override
    public boolean hasPermission(final CommandSource source, final String permission) {
        return source.hasPermission(permission);
    }

    @Override
    public Audience audience(final CommandSource source) {
        return source;
    }
}
