package net.thenextlvl.commander.velocity.implementation;

import lombok.RequiredArgsConstructor;
import net.thenextlvl.commander.CommandFinder;
import net.thenextlvl.commander.velocity.CommanderPlugin;
import org.jspecify.annotations.NullMarked;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NullMarked
@RequiredArgsConstructor
public class ProxyCommandFinder implements CommandFinder {
    private final CommanderPlugin plugin;

    @Override
    public Set<String> findCommands(String input) {
        return findCommands(plugin.server().getCommandManager().getAliases().stream(), input);
    }

    @Override
    public Set<String> findCommands(Stream<String> commands, String input) {
        var pattern = Pattern.compile(input.replace("*", ".*"));
        return commands.filter(command ->
                pattern.matcher(command).matches()
        ).collect(Collectors.toSet());
    }
}
