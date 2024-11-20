package net.thenextlvl.commander.velocity.version;

import com.velocitypowered.api.plugin.Plugin;
import core.version.SemanticVersion;
import core.version.hangar.HangarVersion;
import core.version.hangar.HangarVersionChecker;
import core.version.hangar.Platform;
import lombok.Getter;
import net.thenextlvl.commander.velocity.CommanderPlugin;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@Getter
@NullMarked
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
        return version.platformDependencies().get(Platform.VELOCITY).stream()
                .anyMatch(plugin.server().getVersion().getVersion()::startsWith);
    }

    public void checkVersion() {
        retrieveLatestSupportedVersion().thenAccept(version -> {
            var logger = plugin.logger();
            if (version.equals(versionRunning)) {
                logger.info("You are running the latest version of Commander");
            } else if (version.compareTo(versionRunning) > 0) {
                logger.warn("An update for Commander is available");
                logger.warn("You are running version {}, the latest supported version is {}", versionRunning, version);
                logger.warn("Update at https://hangar.papermc.io/TheNextLvl/{}", getSlug());
            } else logger.warn("You are running a snapshot version of Commander");
        }).exceptionally(throwable -> {
            plugin.logger().error("Version check failed", throwable);
            return null;
        });
    }
}
