package net.thenextlvl.commander.paper.version;

import net.thenextlvl.commander.paper.CommanderPlugin;
import net.thenextlvl.version.SemanticVersion;
import net.thenextlvl.version.modrinth.paper.PaperModrinthVersionChecker;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class CommanderVersionChecker extends PaperModrinthVersionChecker<SemanticVersion> {
    public CommanderVersionChecker(CommanderPlugin plugin) {
        super(plugin, "USLuwMUi");
    }

    @Override
    public @Nullable SemanticVersion parseVersion(String version) {
        return SemanticVersion.parse(version);
    }
}
