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

import de.minestar.krypteia.core.KrypteiaCore;
import de.minestar.krypteia.data.ScanType;
import de.minestar.krypteia.thread.AnalyzeThread;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.ConsoleUtils;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class cmdKrypteiaAnalyze extends AbstractCommand {

    public cmdKrypteiaAnalyze(String syntax, String arguments, String node) {
        super(KrypteiaCore.NAME, syntax, arguments, node);
    }

    @Override
    public void execute(String[] args, Player player) {

        PlayerUtils.sendError(player, pluginName, "Befehl kann nur von der Konsole ausgeführt werden!");
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

        int radius = 0;
        try {
            radius = Integer.parseInt(args[1]);
        } catch (Exception e) {
            ConsoleUtils.printError(pluginName, args[1] + " ist keine Zahl!");
            return;
        }

        ScanType type = ScanType.getType(args[2]);
        if (type == null) {
            ConsoleUtils.printError(pluginName, "Unbekannter ScanType '" + args[2] + "'!");
            return;
        }

        switch (type) {
            case BLOCK :
                if (!KrypteiaCore.dbHandler.hasBlockData(worldName)) {
                    ConsoleUtils.printError(pluginName, "Uber die Welt '" + worldName + "' liegen keine Daten vor!");
                    return;
                }
                break;
            case MOB :
                if (!KrypteiaCore.dbHandler.hasMobData(worldName)) {
                    ConsoleUtils.printError(pluginName, "Uber die Welt '" + worldName + "' liegen keine Daten vor!");
                    return;
                }
                break;
        }

        Bukkit.getScheduler().scheduleAsyncDelayedTask(KrypteiaCore.INSTANCE, new AnalyzeThread(world.getName().toLowerCase(), radius, type));

        ConsoleUtils.printInfo(pluginName, "Start analyzing '" + type.getTypeName() + "' of world '" + worldName + "' with radius " + radius + "!");
    }
}
