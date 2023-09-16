package net.thenextlvl.commander.implementation.command;

import net.thenextlvl.commander.api.command.CommandRegistry;
import net.thenextlvl.commander.implementation.CraftCommander;

import java.io.File;

public class CraftCommandRegistry extends CommandRegistry {
    public CraftCommandRegistry(CraftCommander commander, File dataFolder) {
        super(commander, dataFolder);
    }
}
