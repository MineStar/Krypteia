package de.minestar.krypteia.core;

import de.minestar.minestarlibrary.AbstractCore;
import de.minestar.minestarlibrary.commands.CommandList;

public class KrypteiaCore extends AbstractCore {

    public final static String NAME = "Krypteia";

    public KrypteiaCore() {
        super(NAME);
    }

    @Override
    protected boolean createCommands() {

        // @formatter:off
        this.cmdList = new CommandList(NAME
                );

        // @formatter:on
        return true;
    }
}
