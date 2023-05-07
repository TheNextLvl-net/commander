package net.thenextlvl.commander.api;

import com.google.gson.reflect.TypeToken;
import core.api.file.format.GsonFile;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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

    public void unregisterCommand(String label) {
        unregisterCommand(label, false);
    }

    public abstract void unregisterCommand(String label, boolean alias);
}
