package net.thenextlvl.commander.api;

import com.google.gson.reflect.TypeToken;
import core.api.file.format.GsonFile;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class CommandManager {
    private final GsonFile<List<String>> removedCommands;

    protected CommandManager(File dataFolder) {
        this(new GsonFile<>(new File(dataFolder, "removed-commands.json"), new ArrayList<>(), new TypeToken<>() {
        }));
        if (!getRemovedCommands().getFile().isFile()) getRemovedCommands().save();
    }

    public void unregisterCommands() {
        getRemovedCommands().getRoot().forEach(this::unregisterCommand);
    }

    public abstract void unregisterCommand(String label);

    public boolean registerCommand(String label) {
        boolean remove = getRemovedCommands().getRoot().remove(label);
        if (remove) getRemovedCommands().save();
        return remove;
    }

    public abstract boolean isCommandRegistered(String label);

    public boolean isCommandUnregistered(String label) {
        if (getRemovedCommands().getRoot().contains(label)) return true;
        var name = resolveCommandName(label);
        return getRemovedCommands().getRoot().contains(name);
    }

    public String resolveCommandName(String label, String fallback) {
        String name = resolveCommandName(label);
        return name != null ? name : fallback;
    }


    public abstract @Nullable String resolveCommandName(String label);
}
