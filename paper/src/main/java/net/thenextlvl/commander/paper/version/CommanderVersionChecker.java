package net.thenextlvl.commander.paper.version;

import core.paper.version.PaperHangarVersionChecker;
import core.version.SemanticVersion;
import net.thenextlvl.commander.paper.CommanderPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class CommanderVersionChecker extends PaperHangarVersionChecker<SemanticVersion> {
    public CommanderVersionChecker(CommanderPlugin plugin) {
        super(plugin, "TheNextLvl", "CommandControl");
    }

    @Override
    public @Nullable SemanticVersion parseVersion(String version) {
        return SemanticVersion.parse(version);
    }
}
