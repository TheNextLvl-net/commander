package net.thenextlvl.commander.velocity.version;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.plugin.Plugin;
import net.thenextlvl.commander.velocity.CommanderPlugin;
import net.thenextlvl.version.SemanticVersion;
import net.thenextlvl.version.modrinth.ModrinthVersion;
import net.thenextlvl.version.modrinth.ModrinthVersionChecker;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class CommanderVersionChecker extends ModrinthVersionChecker<SemanticVersion> {
    private final SemanticVersion versionRunning;
    private final CommanderPlugin plugin;

    public CommanderVersionChecker(CommanderPlugin plugin) {
        super("USLuwMUi");
        this.plugin = plugin;
        var version = plugin.getClass().getAnnotation(Plugin.class).version();
        this.versionRunning = Objects.requireNonNull(parseVersion(version));
    }

    @Override
    public SemanticVersion getVersionRunning() {
        return versionRunning;
    }

    @Override
    public SemanticVersion parseVersion(String version) {
        return SemanticVersion.parse(version);
    }

    @Override
    public boolean isSupported(ModrinthVersion version) {
        return version.gameVersions().contains(ProtocolVersion.MAXIMUM_VERSION.getMostRecentSupportedVersion())
               && version.loaders().contains("velocity");
    }

    public void checkVersion() {
        retrieveLatestSupportedVersion().thenAccept(optional -> optional.ifPresentOrElse(this::printVersionInfo,
                () -> retrieveLatestVersion().thenAccept(this::printUnsupportedInfo).exceptionally(throwable -> {
                    plugin.logger().warn("There are no public releases for this plugin yet");
                    return null;
                })
        )).exceptionally(throwable -> {
            plugin.logger().error("Version check failed", throwable);
            return null;
        });
    }

    private void printUnsupportedInfo(SemanticVersion version) {
        var logger = plugin.logger();
        var latest = ProtocolVersion.MAXIMUM_VERSION.getMostRecentSupportedVersion();
        if (version.equals(versionRunning)) {
            logger.warn("{} seems to be unsupported by Commander version {}", latest, versionRunning);
        } else if (version.compareTo(versionRunning) > 0) {
            logger.warn("A new version for Commander is available but {} seems to be unsupported", latest);
            logger.warn("You are running version {}, the latest version is {}", versionRunning, version);
            logger.warn("Update at https://modrinth.com/project/{}", getId());
            logger.warn("Do not test in production and always make backups before updating");
        } else logger.warn("You are running a snapshot version of Commander");
    }

    private void printVersionInfo(SemanticVersion version) {
        var logger = plugin.logger();
        if (version.equals(versionRunning)) {
            logger.info("You are running the latest version of Commander");
        } else if (version.compareTo(versionRunning) > 0) {
            logger.warn("An update for Commander is available");
            logger.warn("You are running version {}, the latest version is {}", versionRunning, version);
            logger.warn("Update at https://modrinth.com/project/{}/?version={}&loader=velocity#download", getId(),
                    ProtocolVersion.MAXIMUM_VERSION.getMostRecentSupportedVersion());
            logger.warn("Do not test in production and always make backups before updating");
        } else logger.warn("You are running a snapshot version of Commander");
    }
}
