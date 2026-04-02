package net.thenextlvl.commander.velocity.implementation;

import net.thenextlvl.commander.CommonCommandRegistry;
import net.thenextlvl.commander.velocity.ProxyCommander;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ProxyCommandRegistry extends CommonCommandRegistry {
    public ProxyCommandRegistry(final ProxyCommander commander) {
        super(commander);
    }

    @Override
    protected boolean internalRegister(final String command) {
        return true;
    }

    @Override
    protected boolean internalUnregister(final String command) {
        ((ProxyCommander) commons).server().getCommandManager().unregister(command);
        return true;
    }
}
