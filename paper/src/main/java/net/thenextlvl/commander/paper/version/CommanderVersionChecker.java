package net.thenextlvl.commander.paper.version;

import core.paper.version.PaperHangarVersionChecker;
import core.version.SemanticVersion;
import lombok.Getter;
import net.thenextlvl.commander.paper.CommanderPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@Getter
@NullMarked
public class CommanderVersionChecker extends PaperHangarVersionChecker<SemanticVersion> {
    private final SemanticVersion versionRunning;

    public CommanderVersionChecker(CommanderPlugin plugin) {
        super(plugin, "TheNextLvl", "CommandControl");
        this.versionRunning = Objects.requireNonNull(parseVersion(plugin.getPluginMeta().getVersion()));
    }

    @Override
    public @Nullable SemanticVersion parseVersion(String version) {
        return SemanticVersion.parse(version);
    }
}
