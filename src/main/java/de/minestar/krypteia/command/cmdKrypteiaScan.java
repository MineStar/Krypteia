/*
 * Copyright (C) 2012 MineStar.de 
 * 
 * This file is part of Krypteia.
 * 
 * Krypteia is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * 
 * Krypteia is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Krypteia.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.minestar.krypteia.command;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import de.minestar.krypteia.core.KrypteiaCore;
import de.minestar.krypteia.data.ScanType;
import de.minestar.krypteia.thread.queues.BlockQueue;
import de.minestar.krypteia.thread.queues.MobQueue;
import de.minestar.krypteia.thread.scan.BlockScanThread;
import de.minestar.krypteia.thread.scan.MobScanThread;
import de.minestar.krypteia.thread.scan.ScanThread;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.ConsoleUtils;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class cmdKrypteiaScan extends AbstractCommand {

    public cmdKrypteiaScan(String syntax, String arguments, String node) {
        super(KrypteiaCore.NAME, syntax, arguments, node);
    }

    @Override
    public void execute(String[] args, Player player) {

        PlayerUtils.sendError(player, pluginName, "Befehl kann nur von der Konsole ausgef�hrt werden!");
        return;
    }

    @Override
    public void execute(String[] args, ConsoleCommandSender console) {

        String worldName = args[0];
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            ConsoleUtils.printError(pluginName, "Welt '" + worldName + "' nicht gefunden!");
            return;
        }

        int size = 0;
        try {
            size = Integer.parseInt(args[1]);
        } catch (Exception e) {
            ConsoleUtils.printError(pluginName, args[1] + " ist keine Zahl!");
            return;
        }

        ScanType type = ScanType.getType(args[2]);
        if (type == null) {
            ConsoleUtils.printError(pluginName, "Unbekannter ScanType '" + args[2] + "'!");
            return;
        }

        ScanThread thread = null;
        switch (type) {
            case BLOCK :
                KrypteiaCore.queue = new BlockQueue();
                thread = new BlockScanThread(world, size);
                break;
            case MOB :
                KrypteiaCore.queue = new MobQueue();
                thread = new MobScanThread(world, size);
                break;
        }

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.scheduleAsyncRepeatingTask(KrypteiaCore.INSTANCE, KrypteiaCore.queue, 0L, 20L * 5L);
        int id = scheduler.scheduleSyncRepeatingTask(KrypteiaCore.INSTANCE, thread, 5L, 20L);
        thread.setThreadId(id);

        ConsoleUtils.printInfo(pluginName, "Start scanning '" + type.getTypeName() + "' in world '" + world.getName() + "' with size " + size);

    }
}
