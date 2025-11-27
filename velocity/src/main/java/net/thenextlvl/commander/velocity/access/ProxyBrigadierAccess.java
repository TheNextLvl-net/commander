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
    public LiteralArgumentBuilder<CommandSource> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    @Override
    public <T> RequiredArgumentBuilder<CommandSource, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    @Override
    public boolean hasPermission(CommandSource source, String permission) {
        return source.hasPermission(permission);
    }

    @Override
    public Audience audience(CommandSource source) {
        return source;
    }
}
