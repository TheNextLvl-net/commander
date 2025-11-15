package net.thenextlvl.commander.paper.implementation;

import net.thenextlvl.commander.CommonCommandFinder;
import net.thenextlvl.commander.paper.PaperCommander;
import org.jspecify.annotations.NullMarked;

import java.util.regex.Pattern;
import java.util.stream.Stream;

@NullMarked
public class PaperCommandFinder extends CommonCommandFinder {
    private final PaperCommander commander;

    public PaperCommandFinder(PaperCommander commander) {
        this.commander = commander;
    }

    @Override
    public Stream<String> findCommands(Pattern pattern) {
        return findCommands(commander.getServer().getCommandMap().getKnownCommands().entrySet()
                .stream().mapMulti((entry, consumer) -> {
                    consumer.accept(entry.getKey());
                    entry.getValue().getAliases().forEach(consumer);
                }), pattern);
    }
}
