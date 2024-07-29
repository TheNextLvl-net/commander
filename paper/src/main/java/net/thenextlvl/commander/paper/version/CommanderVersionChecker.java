package net.thenextlvl.commander.paper.version;

import core.paper.version.PaperHangarVersionChecker;
import core.version.SemanticVersion;
import lombok.Getter;
import net.thenextlvl.commander.paper.CommanderPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
@SuppressWarnings("UnstableApiUsage")
public class CommanderVersionChecker extends PaperHangarVersionChecker<SemanticVersion> {
    private final SemanticVersion versionRunning;

    public CommanderVersionChecker(CommanderPlugin plugin) {
        super("CommandControl");
        this.versionRunning = Objects.requireNonNull(parseVersion(plugin.getPluginMeta().getVersion()));
    }

    @Override
    public @Nullable SemanticVersion parseVersion(String version) {
        return SemanticVersion.parse(version);
    }
}
