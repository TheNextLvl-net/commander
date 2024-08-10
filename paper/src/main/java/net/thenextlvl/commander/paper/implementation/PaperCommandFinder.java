package net.thenextlvl.commander.paper.implementation;

import lombok.RequiredArgsConstructor;
import net.thenextlvl.commander.CommandFinder;
import net.thenextlvl.commander.paper.CommanderPlugin;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class PaperCommandFinder implements CommandFinder {
    private final CommanderPlugin plugin;

    public Set<String> findCommands(String input) {
        return findCommands(plugin.getServer().getCommandMap().getKnownCommands().entrySet()
                .stream().mapMulti((entry, consumer) -> {
                    consumer.accept(entry.getKey());
                    entry.getValue().getAliases().forEach(consumer);
                }), input);
    }

    public Set<String> findCommands(Stream<String> commands, String input) {
        var pattern = Pattern.compile(input.replace("*", ".*"));
        return commands.filter(command ->
                pattern.matcher(command).matches()
        ).collect(Collectors.toSet());
    }
}
