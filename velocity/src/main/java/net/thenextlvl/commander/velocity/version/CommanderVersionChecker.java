package net.thenextlvl.commander.velocity.version;

import com.velocitypowered.api.plugin.Plugin;
import core.version.HangarVersionChecker;
import core.version.SemanticVersion;
import lombok.Getter;
import me.mrafonso.hangar4j.impl.Platform;
import me.mrafonso.hangar4j.impl.version.HangarVersion;
import net.thenextlvl.commander.velocity.CommanderPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class CommanderVersionChecker extends HangarVersionChecker<SemanticVersion> {
    private final SemanticVersion versionRunning;
    private final CommanderPlugin plugin;

    public CommanderVersionChecker(CommanderPlugin plugin) {
        super("CommandControl");
        this.plugin = plugin;
        var version = plugin.getClass().getAnnotation(Plugin.class).version();
        this.versionRunning = Objects.requireNonNull(parseVersion(version));
    }

    @Override
    public @Nullable SemanticVersion parseVersion(String version) {
        return SemanticVersion.parse(version);
    }

    @Override
    public boolean isSupported(HangarVersion version) {
        return version.platformDependencies().get(Platform.VELOCITY)
                .contains(plugin.server().getVersion().getVersion());
    }
}
