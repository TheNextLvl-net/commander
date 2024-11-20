package net.thenextlvl.commander.paper.config;

import com.google.gson.annotations.SerializedName;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record PluginConfig(
        @SerializedName("unknown-command-message") boolean unknownCommandMessage
) {
}
