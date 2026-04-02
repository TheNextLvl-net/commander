package net.thenextlvl.commander.paper.access;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.audience.Audience;
import net.thenextlvl.commander.access.BrigadierAccess;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PaperBrigadierAccess extends BrigadierAccess<CommandSourceStack> {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> literal(final String name) {
        return Commands.literal(name);
    }

    @Override
    public <T> RequiredArgumentBuilder<CommandSourceStack, T> argument(final String name, final ArgumentType<T> type) {
        return Commands.argument(name, type);
    }

    @Override
    public boolean hasPermission(final CommandSourceStack source, final String permission) {
        return source.getSender().hasPermission(permission);
    }

    @Override
    public Audience audience(final CommandSourceStack source) {
        return source.getSender();
    }
}
