package net.thenextlvl.commander.paper.implementation;

import net.thenextlvl.commander.CommonCommandFinder;
import net.thenextlvl.commander.paper.PaperCommander;
import org.jspecify.annotations.NullMarked;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@NullMarked
public class PaperCommandFinder extends CommonCommandFinder {
    private final PaperCommander commander;

    public PaperCommandFinder(PaperCommander commander) {
        this.commander = commander;
    }

    @Override
    public Stream<String> findCommands(final Pattern pattern) {
        final var entries = commander.getServer().getCommandMap().getKnownCommands().entrySet();
        return findCommands(Set.copyOf(entries).stream().mapMulti((entry, consumer) -> {
            consumer.accept(entry.getKey());
            entry.getValue().getAliases().forEach(consumer);
        }), pattern);
    }
}
