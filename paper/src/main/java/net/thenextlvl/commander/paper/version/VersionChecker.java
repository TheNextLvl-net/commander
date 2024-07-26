package net.thenextlvl.commander.paper.version;

import me.mrafonso.hangar4j.HangarClient;
import me.mrafonso.hangar4j.impl.Platform;
import me.mrafonso.hangar4j.impl.version.HangarVersion;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class VersionChecker {
    private final HangarClient hangarClient = new HangarClient(null);

    public void retrieveLatestSupportedVersion(Consumer<Optional<Version>> success) {
        Objects.requireNonNull(hangarClient.getVersions("CommandControl"))
                .thenAcceptAsync(versions -> success.accept(versions.result().stream()
                        .filter(this::isSupported)
                        .map(Version::parse)
                        .filter(Objects::nonNull)
                        .max(Version::compareTo)));
    }

    private boolean isSupported(HangarVersion version) {
        return version.platformDependencies().get(Platform.PAPER)
                .contains(Bukkit.getMinecraftVersion());
    }

    public record Version(int major, int minor, int build) implements Comparable<Version> {

        public static @Nullable Version parse(HangarVersion version) {
            return parse(version.name());
        }

        public static @Nullable Version parse(String string) {
            try {
                var split = string.split("\\.", 3);
                return new Version(
                        Integer.parseInt(split[0]),
                        Integer.parseInt(split[1]),
                        Integer.parseInt(split[2])
                );
            } catch (Exception ignored) {
                return null;
            }
        }

        @Override
        public int compareTo(@NotNull Version version) {
            return major() != version.major() ? Integer.compare(major(), version.major())
                    : minor() != version.minor() ? Integer.compare(minor(), version.minor())
                    : Integer.compare(build(), version.build());
        }

        @Override
        public String toString() {
            return major() + "." + minor() + "." + build();
        }
    }
}
