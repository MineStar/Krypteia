package de.minestar.krypteia.core;

import java.io.File;

import org.bukkit.scheduler.BukkitScheduler;

import de.minestar.krypteia.command.cmdAnalyze;
import de.minestar.krypteia.command.cmdScan;
import de.minestar.krypteia.database.DatabaseHandler;
import de.minestar.krypteia.thread.BlockQueue;
import de.minestar.minestarlibrary.AbstractCore;
import de.minestar.minestarlibrary.commands.CommandList;

public class KrypteiaCore extends AbstractCore {

    public final static String NAME = "Krypteia";

    public static KrypteiaCore INSTANCE;

    public static DatabaseHandler dbHandler;

    public static BlockQueue blockQueue;

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
                    
                    new cmdScan(        "/scan",    "<World> <Size>",   "krypteia.command"),
                    new cmdAnalyze(     "/analyze", "<World> <Radius>", "krypteia.command")
                );

        // @formatter:on
        return true;
    }

    @Override
    protected boolean createThreads() {
        blockQueue = new BlockQueue();

        return true;
    }

    @Override
    protected boolean commonDisable() {
        blockQueue.finishQueue();
        dbHandler.closeConnection();
        return dbHandler != null && !dbHandler.hasConnection();
    }

    @Override
    protected boolean startThreads(BukkitScheduler scheduler) {

        scheduler.scheduleAsyncRepeatingTask(this, blockQueue, 0L, 20L * 5L);

        return true;
    }
}
