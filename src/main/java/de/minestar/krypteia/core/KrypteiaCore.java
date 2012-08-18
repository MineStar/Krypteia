package de.minestar.krypteia.core;

import java.io.File;

import de.minestar.krypteia.command.cmdKrypteia;
import de.minestar.krypteia.command.cmdKrypteiaAnalyze;
import de.minestar.krypteia.command.cmdKrypteiaScan;
import de.minestar.krypteia.database.DatabaseHandler;
import de.minestar.krypteia.thread.queues.DatabaseQueue;
import de.minestar.minestarlibrary.AbstractCore;
import de.minestar.minestarlibrary.commands.CommandList;

public class KrypteiaCore extends AbstractCore {

    public final static String NAME = "Krypteia";

    public static KrypteiaCore INSTANCE;

    public static DatabaseHandler dbHandler;

    public static DatabaseQueue queue;

    public KrypteiaCore() {
        super(NAME);
        INSTANCE = this;
    }

    @Override
    protected boolean createManager() {

        dbHandler = new DatabaseHandler(NAME, new File(getDataFolder(), "sqlconfig.yml"));

        return dbHandler != null && dbHandler.hasConnection();
    }

    @Override
    protected boolean createCommands() {

        // @formatter:off
        this.cmdList = new CommandList(NAME,
                    
                new cmdKrypteia(NAME,   "/krypt",   "",     "krypteia.command", 
                    new cmdKrypteiaScan(            "scan",    "<World> <Size> <Type>",   "krypteia.command.scan"),
                    new cmdKrypteiaAnalyze(         "analyze", "<World> <Radius> <Type>", "krypteia.command.analyze")
                )
        );

        // @formatter:on
        return true;
    }

    @Override
    protected boolean commonDisable() {
        if (queue != null)
            queue.finishQueue();
        dbHandler.closeConnection();
        return dbHandler != null && !dbHandler.hasConnection();
    }
}
